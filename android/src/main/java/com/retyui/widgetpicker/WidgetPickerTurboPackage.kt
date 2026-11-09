package com.retyui.widgetpicker

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class WidgetPickerTurboPackage : BaseReactPackage() {
    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
        when (name) {
            WidgetPickerModule.NAME -> WidgetPickerModule(reactContext)
            else -> null
        }

    override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
        mapOf(
            WidgetPickerModule.NAME to
                ReactModuleInfo(
                    name = WidgetPickerModule.NAME,
                    className = WidgetPickerModule::class.java.name,
                    canOverrideExistingModule = false,
                    needsEagerInit = false,
                    isCxxModule = false,
                    isTurboModule = true,
                )
        )
    }
}
