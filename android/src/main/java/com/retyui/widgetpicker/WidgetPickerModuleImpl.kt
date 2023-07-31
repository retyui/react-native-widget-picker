package com.retyui.widgetpicker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext

import com.facebook.react.bridge.Arguments

class WidgetPickerModuleImpl(private val reactContext: ReactApplicationContext) {
    fun isRequestPinAppWidgetSupported(): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager: AppWidgetManager = reactContext.getSystemService(AppWidgetManager::class.java)
                return appWidgetManager.isRequestPinAppWidgetSupported
            }
            return false
        } catch (ignored: Exception) {
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestPinAppWidget(widgetClassKey: String, promise: Promise) {
        try {
            val appWidgetManager: AppWidgetManager = reactContext.getSystemService(AppWidgetManager::class.java)
            val myProvider = ComponentName(reactContext, widgets[widgetClassKey]!!)
            val result = appWidgetManager.requestPinAppWidget(myProvider, null, null)
            if(result){
                promise.resolve(Arguments.createMap().apply {
                    putString("message", "success")
                })
            }else{
                promise.resolve(Arguments.createMap().apply {
                    // launcher doesn't support this feature
                    // or widget not found
                    putString("message", "cannot pin widget")
                })
            }

        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    companion object {
        const val NAME = "WidgetPickerModule"

        private var widgets = mutableMapOf<String, Class<*>>()

        @JvmStatic
        fun registerWidgetClass(key: String, classValue: Class<*>) {
            widgets.remove(key);
            widgets[key] = classValue;
        }
    }
}
