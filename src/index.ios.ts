export const WidgetPicker = {
    isRequestPinAppWidgetSupported: () => false,
    requestPinAppWidget: (widgetClassKey: string) => Promise.resolve({message: 'not supported on ios'})
} as const
