import 'package:flutter_test/flutter_test.dart';
import 'package:music_tag/music_tag.dart';
import 'package:music_tag/music_tag_platform_interface.dart';
import 'package:music_tag/music_tag_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockMusicTagPlatform
    with MockPlatformInterfaceMixin
    implements MusicTagPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<Map<String, String>> readTag(String filePath) {
    throw UnimplementedError();
  }

  @override
  Future<void> writeTag(String filePath, Map<String, String> tags) {
    throw UnimplementedError();
  }
}

void main() {
  final MusicTagPlatform initialPlatform = MusicTagPlatform.instance;

  test('$MethodChannelMusicTag is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelMusicTag>());
  });

  test('getPlatformVersion', () async {
    MusicTag musicTagPlugin = MusicTag();
    MockMusicTagPlatform fakePlatform = MockMusicTagPlatform();
    MusicTagPlatform.instance = fakePlatform;

    expect(await musicTagPlugin.getPlatformVersion(), '42');
  });
}
