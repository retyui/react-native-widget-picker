// swift-tools-version: 6.0
//
// Swift Package Manager support (React Native 0.87+). CocoaPods keeps using
// react-native-widget-picker.podspec, which compiles the same `ios/` sources.
//
// Hand-maintained ("self-managed"): RN's SPM autolinker uses a checked-in
// Package.swift as-is (see `findSelfManagedPackageDir` in
// react-native/scripts/spm/generate-spm-autolinking.js). It has to be, because
// SPM can't mix Swift and ObjC++ in one target, so the sources are split:
//   ios/WidgetPickerSwift  — WidgetKit calls (Swift)
//   ios/WidgetPicker       — the TurboModule (ObjC++), depends on the above
//
// Contracts the autolinker imposes:
// 1. Package, library product and its target are all named
//    `ReactNativeWidgetPicker` (pinned by `swiftpmConfig.name` in package.json).
// 2. The `.package(path:)` references are fixed relative paths: the autolinker
//    links this package at <app>/build/generated/autolinking/libs/<name>, so
//    React Native's packages always sit at these depths from it.

import PackageDescription

let package = Package(
    name: "ReactNativeWidgetPicker",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "ReactNativeWidgetPicker", targets: ["ReactNativeWidgetPicker"]),
    ],
    dependencies: [
        .package(name: "ReactNative", path: "../../../../xcframeworks"),
        .package(name: "React-GeneratedCode", path: "../../../ios"),
    ],
    targets: [
        .target(
            name: "ReactNativeWidgetPickerSwift",
            path: "ios/WidgetPickerSwift",
            linkerSettings: [.linkedFramework("WidgetKit")]
        ),
        .target(
            name: "ReactNativeWidgetPicker",
            dependencies: [
                "ReactNativeWidgetPickerSwift",
                .product(name: "ReactHeaders", package: "ReactNative"),
                .product(name: "ReactNativeHeaders", package: "ReactNative"),
                .product(name: "ReactNativeDependenciesHeaders", package: "ReactNative"),
                .product(name: "ReactAppHeaders", package: "React-GeneratedCode"),
            ],
            path: "ios/WidgetPicker",
            publicHeadersPath: ".",
            cxxSettings: [
                // Must match the prebuilt React framework's config-gated C++ ABI.
                .define("DEBUG", .when(configuration: .debug)),
                .define("NDEBUG", .when(configuration: .release)),
            ]
        ),
    ],
    swiftLanguageModes: [.v5],
    cxxLanguageStandard: .cxx20
)
