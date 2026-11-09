package com.retyui.widgetpicker

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = WidgetPickerModule.NAME)
class WidgetPickerModule(reactContext: ReactApplicationContext) :
    NativeWidgetPickerModuleSpec(reactContext) {
    private val moduleImpl = WidgetPickerModuleImpl(reactContext)

    override fun getName() = NAME

    override fun isRequestPinAppWidgetSupported() = moduleImpl.isRequestPinAppWidgetSupported()

    override fun requestPinAppWidget(
        widgetClassKey: String,
        options: ReadableMap?,
        promise: Promise,
    ) = moduleImpl.requestPinAppWidget(widgetClassKey, options, promise)

    override fun getPinnedWidgetIds(widgetClassKey: String, promise: Promise) =
        moduleImpl.getPinnedWidgetIds(widgetClassKey, promise)

    override fun invalidate() {
        moduleImpl.invalidate()
        super.invalidate()
    }

    companion object {
        const val NAME = WidgetPickerModuleImpl.NAME
    }
}
