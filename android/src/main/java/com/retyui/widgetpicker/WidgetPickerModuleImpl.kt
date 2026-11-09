package com.retyui.widgetpicker

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.UiThreadUtil
import java.util.concurrent.ConcurrentHashMap

class WidgetPickerModuleImpl(private val reactContext: ReactApplicationContext) :
    LifecycleEventListener {

    /** A pin request waiting for the user's decision. Only touched on the main thread. */
    private class PendingPin(
        val promise: Promise,
        val provider: ComponentName,
        val idsBefore: Set<Int>,
    ) {
        var sawPause = false
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private var pending: PendingPin? = null
    private var receiverRegistered = false

    private val appWidgetManager: AppWidgetManager
        get() = AppWidgetManager.getInstance(reactContext)

    /** Fired by the launcher (via successCallback) once the widget is actually placed. */
    private val pinnedReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id =
                    intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID,
                    )
                if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    settle(id)
                } else {
                    settleFromWidgetIds()
                }
            }
        }

    init {
        reactContext.addLifecycleEventListener(this)
    }

    fun isRequestPinAppWidgetSupported(): Boolean =
        try {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                appWidgetManager.isRequestPinAppWidgetSupported
        } catch (ignored: Exception) {
            false
        }

    fun requestPinAppWidget(widgetClassKey: String, options: ReadableMap?, promise: Promise) {
        UiThreadUtil.runOnUiThread {
            try {
                if (
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
                        !appWidgetManager.isRequestPinAppWidgetSupported
                ) {
                    promise.reject(E_NOT_SUPPORTED, "Launcher doesn't support pinning widgets")
                    return@runOnUiThread
                }

                val provider = resolveProvider(widgetClassKey)
                if (provider == null) {
                    promise.reject(E_WIDGET_NOT_FOUND, "Widget \"$widgetClassKey\" not found")
                    return@runOnUiThread
                }

                val extras =
                    options
                        ?.takeIf { it.hasKey("previewLayout") }
                        ?.getString("previewLayout")
                        ?.let { layoutName ->
                            @Suppress("DiscouragedApi")
                            val layoutId =
                                reactContext.resources.getIdentifier(
                                    layoutName,
                                    "layout",
                                    reactContext.packageName,
                                )
                            if (layoutId == 0) {
                                promise.reject(
                                    E_INVALID_PREVIEW,
                                    "Layout \"$layoutName\" not found",
                                )
                                return@runOnUiThread
                            }
                            Bundle().apply {
                                putParcelable(
                                    AppWidgetManager.EXTRA_APPWIDGET_PREVIEW,
                                    RemoteViews(reactContext.packageName, layoutId),
                                )
                            }
                        }

                // Only one dialog can be shown at a time: settle a previous request first
                settleFromWidgetIds()

                registerReceiverIfNeeded()
                pending =
                    PendingPin(
                        promise,
                        provider,
                        appWidgetManager.getAppWidgetIds(provider).toSet(),
                    )

                val requested =
                    appWidgetManager.requestPinAppWidget(provider, extras, createSuccessCallback())
                if (!requested) {
                    pending = null
                    unregisterReceiverIfNeeded()
                    promise.reject(E_PIN_FAILED, "Launcher refused to pin the widget")
                }
            } catch (e: Exception) {
                if (pending?.promise === promise) {
                    pending = null
                    unregisterReceiverIfNeeded()
                }
                promise.reject(e)
            }
        }
    }

    fun getPinnedWidgetIds(widgetClassKey: String, promise: Promise) {
        try {
            val provider = resolveProvider(widgetClassKey)
            if (provider == null) {
                promise.reject(E_WIDGET_NOT_FOUND, "Widget \"$widgetClassKey\" not found")
                return
            }
            val result = Arguments.createArray()
            appWidgetManager.getAppWidgetIds(provider).forEach { result.pushInt(it) }
            promise.resolve(result)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    fun invalidate() {
        reactContext.removeLifecycleEventListener(this)
        UiThreadUtil.runOnUiThread { settleFromWidgetIds() }
    }

    override fun onHostPause() {
        pending?.sawPause = true
    }

    override fun onHostResume() {
        val current = pending ?: return
        if (!current.sawPause) return
        // The pin dialog closed. Give the launcher a moment to bind the widget
        // (and deliver successCallback) before falling back to comparing IDs.
        mainHandler.postDelayed(
            { if (pending === current) settleFromWidgetIds() },
            RESUME_GRACE_PERIOD_MS,
        )
    }

    override fun onHostDestroy() = Unit

    /** Explicit (package-scoped) mutable intent: required for targetSdk 34+. */
    private fun createSuccessCallback(): PendingIntent {
        val intent = Intent(actionPinned()).setPackage(reactContext.packageName)
        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // The launcher fills in EXTRA_APPWIDGET_ID
            flags = flags or PendingIntent.FLAG_MUTABLE
        }
        return PendingIntent.getBroadcast(reactContext, 0, intent, flags)
    }

    // internal (not private): called from pinnedReceiver without a synthetic accessor
    internal fun settle(appWidgetId: Int) {
        val current = pending ?: return
        pending = null
        unregisterReceiverIfNeeded()
        current.promise.resolve(
            Arguments.createMap().apply {
                putString("status", "pinned")
                putInt("appWidgetId", appWidgetId)
            }
        )
    }

    internal fun settleFromWidgetIds() {
        val current = pending ?: return
        val newId =
            try {
                appWidgetManager.getAppWidgetIds(current.provider).firstOrNull {
                    it !in current.idsBefore
                }
            } catch (ignored: Exception) {
                null
            }
        if (newId != null) {
            settle(newId)
            return
        }
        pending = null
        unregisterReceiverIfNeeded()
        current.promise.resolve(Arguments.createMap().apply { putString("status", "cancelled") })
    }

    private fun registerReceiverIfNeeded() {
        if (receiverRegistered) return
        val filter = IntentFilter(actionPinned())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            reactContext.registerReceiver(pinnedReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            reactContext.registerReceiver(pinnedReceiver, filter)
        }
        receiverRegistered = true
    }

    private fun unregisterReceiverIfNeeded() {
        if (!receiverRegistered) return
        receiverRegistered = false
        try {
            reactContext.unregisterReceiver(pinnedReceiver)
        } catch (ignored: IllegalArgumentException) {}
    }

    private fun actionPinned() = "${reactContext.packageName}.widgetpicker.ACTION_WIDGET_PINNED"

    /**
     * Resolves a key to one of this app's widget providers: an explicitly registered class first,
     * then any provider of this package whose class name (simple or fully qualified) matches.
     */
    private fun resolveProvider(widgetClassKey: String): ComponentName? {
        widgets[widgetClassKey]?.let {
            return ComponentName(reactContext, it)
        }
        val packageName = reactContext.packageName
        val providers =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appWidgetManager.getInstalledProvidersForPackage(packageName, null)
            } else {
                appWidgetManager.installedProviders.filter {
                    it.provider.packageName == packageName
                }
            }
        return providers
            .map { it.provider }
            .firstOrNull {
                it.className == widgetClassKey ||
                    it.className.substringAfterLast('.') == widgetClassKey
            }
    }

    companion object {
        const val NAME = "WidgetPickerModule"

        const val E_NOT_SUPPORTED = "E_NOT_SUPPORTED"
        const val E_WIDGET_NOT_FOUND = "E_WIDGET_NOT_FOUND"
        const val E_INVALID_PREVIEW = "E_INVALID_PREVIEW"
        const val E_PIN_FAILED = "E_PIN_FAILED"

        private const val RESUME_GRACE_PERIOD_MS = 500L

        private val widgets = ConcurrentHashMap<String, Class<*>>()

        /**
         * Optional: map a custom key to a widget class. Without it, widgets are found by their
         * class name automatically.
         */
        @JvmStatic
        fun registerWidgetClass(key: String, classValue: Class<*>) {
            widgets[key] = classValue
        }
    }
}
