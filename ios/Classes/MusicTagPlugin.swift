import Flutter
import UIKit
import AVFoundation

public class SwiftMusicTagPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "music_tag", binaryMessenger: registrar.messenger())
    let instance = SwiftMusicTagPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if call.method == "writeTag" {
      guard let args = call.arguments as? [String: Any],
            let filePath = args["filePath"] as? String,
            let tags = args["tags"] as? [String: String] else {
        result(FlutterError(code: "INVALID_ARGUMENT", message: "Invalid argument", details: nil))
        return
      }
      writeTag(filePath: filePath, tags: tags)
      result(nil)
    } else if call.method == "readTag" {
      guard let args = call.arguments as? [String: Any],
            let filePath = args["filePath"] as? String else {
        result(FlutterError(code: "INVALID_ARGUMENT", message: "Invalid argument", details: nil))
        return
      }
      let tags = readTag(filePath: filePath)
      result(tags)
    } else {
      result(FlutterMethodNotImplemented)
    }
  }

  private func writeTag(filePath: String, tags: [String: String]) {
    // 使用 AVFoundation 来写入标签
  }

  private func readTag(filePath: String) -> [String: String] {
    // 使用 AVFoundation 来读取标签
    return [:]
  }
}

