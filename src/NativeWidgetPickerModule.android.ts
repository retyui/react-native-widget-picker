import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

export interface Spec extends TurboModule {
  isRequestPinAppWidgetSupported(): boolean;

  requestPinAppWidget(
    widgetClassKey: string,
    options: { previewLayout?: string } | null,
  ): Promise<{ status: string; appWidgetId?: number }>;

  getPinnedWidgetIds(widgetClassKey: string): Promise<number[]>;
}

export default TurboModuleRegistry.getEnforcing<Spec>("WidgetPickerModule");
