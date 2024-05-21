package com.example.music_tag

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class MusicTagPlugin : FlutterPlugin, MethodCallHandler {
  private var channel: MethodChannel? = null
  fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getBinaryMessenger(), "music_tag")
    channel.setMethodCallHandler(this)
  }

  fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method.equals("writeTag")) {
      val filePath: String = call.argument("filePath")
      val tags: Map<String, String> = call.argument("tags")
      writeTag(filePath, tags)
      result.success(null)
    } else if (call.method.equals("readTag")) {
      val filePath: String = call.argument("filePath")
      val tags = readTag(filePath)
      result.success(tags)
    } else {
      result.notImplemented()
    }
  }

  private fun writeTag(filePath: String, tags: Map<String, String>) {
    // 使用 Java 库来写入标签，例如 jaudiotagger
  }

  private fun readTag(filePath: String): Map<String, String> {
    // 使用 Java 库来读取标签，例如 jaudiotagger
    return HashMap()
  }

  fun onDetachedFromEngine(binding: FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}

