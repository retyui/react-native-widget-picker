import Foundation
import WidgetKit

@objc(WidgetPickerIOSImpl)
public final class WidgetPickerIOSImpl: NSObject {
  @objc public static func getCurrentWidgetConfigurations(
    resolve: @escaping ([[String: String]]) -> Void,
    reject: @escaping (String, String, Error?) -> Void
  ) {
    WidgetCenter.shared.getCurrentConfigurations { result in
      switch result {
      case .success(let widgets):
        resolve(widgets.map { ["kind": $0.kind, "family": String(describing: $0.family)] })
      case .failure(let error):
        reject("E_GET_WIDGETS_FAILED", error.localizedDescription, error)
      }
    }
  }
}
