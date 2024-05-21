import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'music_tag_method_channel.dart';

abstract class MusicTagPlatform extends PlatformInterface {
  /// Constructs a MusicTagPlatform.
  MusicTagPlatform() : super(token: _token);

  static final Object _token = Object();

  static MusicTagPlatform _instance = MethodChannelMusicTag();

  /// The default instance of [MusicTagPlatform] to use.
  ///
  /// Defaults to [MethodChannelMusicTag].
  static MusicTagPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MusicTagPlatform] when
  /// they register themselves.
  static set instance(MusicTagPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<void> writeTag(String filePath, Map<String, String> tags) {
    throw UnimplementedError('writeTag() has not been implemented.');
  }

  Future<Map<String, String>> readTag(String filePath) {
    throw UnimplementedError('readTag() has not been implemented.');
  }
}
