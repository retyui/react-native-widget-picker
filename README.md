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
* support React Native's New & Old Architecture

```shell
yarn add react-native-widget-picker
# or
npm install react-native-widget-picker
```

Edit `android/app/src/main/java/com/.../MainActivity.java` and add:

```diff
+import com.retyui.widgetpicker.WidgetPickerModuleImpl;

public class MainActivity extends ReactActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
+    WidgetPickerModuleImpl.registerWidgetClass("MyAppWidget", MyAppWidget.class);
    // you can register multiple widgets ^^^ 
  }
```

## Usage

```tsx
import {WidgetPicker} from 'react-native-widget-picker';

WidgetPicker.isRequestPinAppWidgetSupported() // true or false

WidgetPicker.requestPinAppWidget("MyAppWidget").then((value) => { // "MyAppWidget" - name from MainActivity.java
    if (value.message === "success") {
        // success
    }
});
```

## License

MIT
