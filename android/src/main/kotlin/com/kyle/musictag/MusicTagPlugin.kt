package com.kyle.musictag

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


class MusicTagPlugin : FlutterPlugin, MethodCallHandler {
    private var channel: MethodChannel? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.getBinaryMessenger(), "music_tag")
        channel!!.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method.equals("writeTag")) {
            val filePath: String? = call.argument("filePath")
            val tags: Map<String, String>? = call.argument("tags")
            if (filePath != null) {
                if (tags != null) {
                    writeTag(filePath, tags)
                }
            }
            result.success(null)
        } else if (call.method.equals("readTag")) {
            val filePath: String? = call.argument("filePath")
            val tags = filePath?.let { readTag(it) }
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

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel!!.setMethodCallHandler(null)
    }
}