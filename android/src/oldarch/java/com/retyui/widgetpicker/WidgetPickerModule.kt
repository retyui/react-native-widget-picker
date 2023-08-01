package com.retyui.widgetpicker

import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = WidgetPickerModule.NAME)
class WidgetPickerModule(
    // Each native module class consumes react application context
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
    // Use shared module implementation and forward react application context
    private val moduleImpl = WidgetPickerModuleImpl(reactContext)

    // Return the name of the module - it should match the name provided in JS specification
    override fun getName() = NAME

    @ReactMethod(isBlockingSynchronousMethod = true)
    fun isRequestPinAppWidgetSupported() = moduleImpl.isRequestPinAppWidgetSupported()

    @ReactMethod
    @RequiresApi(Build.VERSION_CODES.O)
    fun requestPinAppWidget(widgetClassKey: String, promise: Promise) = moduleImpl.requestPinAppWidget(widgetClassKey, promise)

    companion object {
        const val NAME = WidgetPickerModuleImpl.NAME
    }
}
