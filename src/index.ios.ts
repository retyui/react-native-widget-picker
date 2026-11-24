import { WidgetPickerErrorCode } from "./types";
import NativeWidgetPickerIOS from "./NativeWidgetPickerIOS";
import type { WidgetConfiguration, WidgetPickerApi } from "./types";

export * from "./types";

const notSupported = () =>
  Promise.reject(
    Object.assign(new Error("Pinning widgets is not supported on iOS"), {
      code: WidgetPickerErrorCode.NOT_SUPPORTED,
    }),
  );

export const WidgetPicker: WidgetPickerApi = {
  isRequestPinAppWidgetSupported: () => false,
  requestPinAppWidget: notSupported,
  getPinnedWidgetIds: () => Promise.resolve([]),
  isWidgetPinned: () => Promise.resolve(false),
  getCurrentWidgetConfigurations: () =>
    NativeWidgetPickerIOS.getCurrentWidgetConfigurations() as Promise<
      WidgetConfiguration[]
    >,
};
