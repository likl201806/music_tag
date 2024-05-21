import 'music_tag_platform_interface.dart';

class MusicTag {
  Future<String?> getPlatformVersion() {
    return MusicTagPlatform.instance.getPlatformVersion();
  }

  Future<void> writeTag(String filePath, Map<String, String> tags) {
    return MusicTagPlatform.instance.writeTag(filePath, tags);
  }

  Future<Map<String, String>> readTag(String filePath) {
    return MusicTagPlatform.instance.readTag(filePath);
  }
}
