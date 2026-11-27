import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

export interface Spec extends TurboModule {
  getCurrentWidgetConfigurations(): Promise<{ kind: string; family: string }[]>;
}

export default TurboModuleRegistry.getEnforcing<Spec>("WidgetPickerIOSModule");
