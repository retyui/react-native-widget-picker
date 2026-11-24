// SPM exposes generated specs through ReactAppHeaders; CocoaPods through the
// codegen library name.
#if __has_include(<ReactCodegen/WidgetPicker/WidgetPicker.h>)
#import <ReactCodegen/WidgetPicker/WidgetPicker.h>
#else
#import <WidgetPicker/WidgetPicker.h>
#endif

// Implemented in Swift (WidgetPickerIOSImpl.swift). Declared here instead of
// importing the generated -Swift.h, whose name and location differ between
// CocoaPods and SPM (where the Swift code is a separate target).
@interface WidgetPickerIOSImpl : NSObject
+ (void)getCurrentWidgetConfigurationsWithResolve:
            (void (^)(NSArray<NSDictionary<NSString *, NSString *> *> *))resolve
                                           reject:(void (^)(NSString *, NSString *, NSError *_Nullable))reject;
@end

@interface WidgetPickerIOSModule : NSObject <NativeWidgetPickerIOSSpec>
@end

@implementation WidgetPickerIOSModule

RCT_EXPORT_MODULE(WidgetPickerIOSModule)

- (void)getCurrentWidgetConfigurations:(RCTPromiseResolveBlock)resolve
                                reject:(RCTPromiseRejectBlock)reject
{
  [WidgetPickerIOSImpl
      getCurrentWidgetConfigurationsWithResolve:^(NSArray<NSDictionary<NSString *, NSString *> *> *widgets) {
        resolve(widgets);
      }
      reject:^(NSString *code, NSString *message, NSError *_Nullable error) {
        reject(code, message, error);
      }];
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeWidgetPickerIOSSpecJSI>(params);
}

@end
