import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'music_tag_platform_interface.dart';

/// An implementation of [MusicTagPlatform] that uses method channels.
class MethodChannelMusicTag extends MusicTagPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('music_tag');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> writeTag(String filePath, Map<String, String> tags) async {
    await methodChannel
        .invokeMethod('writeTag', {'filePath': filePath, 'tags': tags});
  }

  @override
  Future<Map<String, String>> readTag(String filePath) async {
    final Map<dynamic, dynamic> tags =
        await methodChannel.invokeMethod('readTag', {'filePath': filePath});
    return tags.cast<String, String>();
  }
}
