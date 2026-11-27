import NativeWidgetPicker from "./NativeWidgetPickerModule.android";
import { WidgetPickerErrorCode } from "./types";
import type { PinWidgetResult, WidgetPickerApi } from "./types";

export * from "./types";

export const WidgetPicker: WidgetPickerApi = {
  isRequestPinAppWidgetSupported: () =>
    NativeWidgetPicker.isRequestPinAppWidgetSupported(),
  requestPinAppWidget: (widgetClassKey, options) =>
    NativeWidgetPicker.requestPinAppWidget(
      widgetClassKey,
      options ?? null,
    ) as Promise<PinWidgetResult>,
  getPinnedWidgetIds: (widgetClassKey) =>
    NativeWidgetPicker.getPinnedWidgetIds(widgetClassKey),
  isWidgetPinned: async (widgetClassKey) =>
    (await NativeWidgetPicker.getPinnedWidgetIds(widgetClassKey)).length > 0,
  getCurrentWidgetConfigurations: () =>
    Promise.reject(
      Object.assign(new Error("getCurrentWidgetConfigurations is iOS only"), {
        code: WidgetPickerErrorCode.NOT_SUPPORTED,
      }),
    ),
};
