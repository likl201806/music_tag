import 'dart:async';
import 'package:flutter/services.dart';

class MusicTag {
  static const MethodChannel _channel = MethodChannel('music_tag_plugin');

  static Future<void> writeTag(
      String filePath, Map<String, String> tags) async {
    await _channel
        .invokeMethod('writeTag', {'filePath': filePath, 'tags': tags});
  }

  static Future<Map<String, String>> readTag(String filePath) async {
    final Map<dynamic, dynamic> tags =
        await _channel.invokeMethod('readTag', {'filePath': filePath});
    return tags.cast<String, String>();
  }
}
