# react-native-widget-picker

[![react-native-widget-picker on npm](https://badgen.net/npm/v/react-native-widget-picker)](https://www.npmjs.com/package/react-native-widget-picker)
[![react-native-widget-picker downloads](https://badgen.net/npm/dm/react-native-widget-picker)](https://www.npmtrends.com/react-native-widget-picker)
[![react-native-widget-picker install size](https://packagephobia.com/badge?p=react-native-widget-picker)](https://packagephobia.com/result?p=react-native-widget-picker)
[![CI status](https://github.com/retyui/react-native-widget-picker/actions/workflows/android_ios.yaml/badge.svg)](https://github.com/retyui/react-native-widget-picker/actions/workflows/android_ios.yaml)

Let users pin a widget. On devices running Android 8.0 (API level 26) and higher, launchers that
let [pin widgets](https://developer.android.com/develop/ui/views/appwidgets/configuration) onto their home screen

https://github.com/retyui/react-native-detect-maestro/assets/4661784/e301602d-d82e-4f5f-b259-1bae3677b868

## Getting started

* Android only
* React Native's New Architecture only (RN 0.80+). Use `1.x` for the old architecture

```shell
yarn add react-native-widget-picker
# or
npm install react-native-widget-picker
```

That's it: widgets are found by their `AppWidgetProvider` class name (simple or fully
qualified), e.g. `"MyAppWidget"` or `"com.myapp.MyAppWidget"`.

### Custom keys (optional)

To use a different key, register the class, e.g. in `MainActivity.kt`:

```kotlin
import com.retyui.widgetpicker.WidgetPickerModuleImpl.Companion.registerWidgetClass

class MainActivity : ReactActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    registerWidgetClass("weather", MyAppWidget::class.java)
  }
}
```

## Usage

```tsx
import { WidgetPicker, WidgetPickerErrorCode } from "react-native-widget-picker";

WidgetPicker.isRequestPinAppWidgetSupported(); // true or false

try {
  const result = await WidgetPicker.requestPinAppWidget("MyAppWidget");

  if (result.status === "pinned") {
    console.log("Added, appWidgetId:", result.appWidgetId);
  } else {
    // result.status === "cancelled": the user dismissed the dialog
  }
} catch (error) {
  switch (error.code) {
    case WidgetPickerErrorCode.NOT_SUPPORTED: // Android < 8 or the launcher can't pin widgets
    case WidgetPickerErrorCode.WIDGET_NOT_FOUND: // no widget matches "MyAppWidget"
    case WidgetPickerErrorCode.INVALID_PREVIEW: // `previewLayout` isn't a layout resource
    case WidgetPickerErrorCode.PIN_FAILED: // the launcher refused the request
  }
}

// Custom preview in the pin dialog (res/layout/my_widget_preview.xml)
await WidgetPicker.requestPinAppWidget("MyAppWidget", {
  previewLayout: "my_widget_preview",
});

// Is the widget already on the home screen? (e.g. to hide an "Add widget" button)
await WidgetPicker.isWidgetPinned("MyAppWidget"); // true or false
await WidgetPicker.getPinnedWidgetIds("MyAppWidget"); // [12, 15]
```

The promise resolves when the user adds the widget or closes the dialog. If the user drags the
widget onto the home screen instead, it resolves once the widget is placed or the app is back in
the foreground.

## Migrating from 1.x

* Old architecture is no longer supported
* `requestPinAppWidget` resolves with `{ status: "pinned", appWidgetId } | { status: "cancelled" }`
  instead of `{ message }`; errors (`"not supported"`, `"widget not found"`, `"cannot pin widget"`)
  now reject with a `code`
* `registerWidgetClass` is optional

## License

MIT
