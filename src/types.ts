export type PinWidgetOptions = {
  /**
   * Name of a layout resource (`res/layout/<name>.xml`) shown as the preview
   * in the launcher's pin dialog. Defaults to the widget's own preview.
   */
  previewLayout?: string;
};

export type PinWidgetResult =
  { status: "pinned"; appWidgetId: number } | { status: "cancelled" };

/** `code` of the error a rejected promise carries. */
export const WidgetPickerErrorCode = {
  /** Android < 8.0, or the launcher can't pin widgets */
  NOT_SUPPORTED: "E_NOT_SUPPORTED",
  /** No widget of this app matches the key */
  WIDGET_NOT_FOUND: "E_WIDGET_NOT_FOUND",
  /** `previewLayout` isn't a layout resource of the app */
  INVALID_PREVIEW: "E_INVALID_PREVIEW",
  /** The launcher refused the request */
  PIN_FAILED: "E_PIN_FAILED",
} as const;

export type WidgetPickerErrorCode =
  (typeof WidgetPickerErrorCode)[keyof typeof WidgetPickerErrorCode];

export interface WidgetPickerApi {
  isRequestPinAppWidgetSupported(): boolean;
  /**
   * Shows the launcher's "add widget" dialog.
   * Resolves once the user adds the widget or dismisses the dialog.
   */
  requestPinAppWidget(
    widgetClassKey: string,
    options?: PinWidgetOptions,
  ): Promise<PinWidgetResult>;
  /** IDs of this widget's instances currently on the home screen. */
  getPinnedWidgetIds(widgetClassKey: string): Promise<number[]>;
  isWidgetPinned(widgetClassKey: string): Promise<boolean>;
}
