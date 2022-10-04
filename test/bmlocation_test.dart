
import 'package:bmlocation/models/flutter_baidu_heading.dart';
import 'package:bmlocation/models/flutter_baidu_location.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:bmlocation/bmlocation.dart';
import 'package:bmlocation/bmlocation_platform_interface.dart';
import 'package:bmlocation/bmlocation_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockBmlocationPlatform
    with MockPlatformInterfaceMixin
    implements BmlocationPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<bool> authAk(String key) {
    // TODO: implement authAk
    throw UnimplementedError();
  }

  @override
  Future<bool> headingAvailable() {
    // TODO: implement headingAvailable
    throw UnimplementedError();
  }


  @override
  Future<bool> prepareLoc(Map androidMap, Map iosMap) {
    // TODO: implement prepareLoc
    throw UnimplementedError();
  }


  @override
  Future<bool> setAgreePrivacy(bool isAgree) {
    // TODO: implement setAgreePrivacy
    throw UnimplementedError();
  }

  @override
  Future<bool> singleLocation(Map arguments) {
    // TODO: implement singleLocation
    throw UnimplementedError();
  }

  @override
  Future<bool> startLocation() {
    // TODO: implement startLocation
    throw UnimplementedError();
  }

  @override
  Future<bool> startUpdatingHeading() {
    // TODO: implement startUpdatingHeading
    throw UnimplementedError();
  }

  @override
  Future<bool> stopLocation() {
    // TODO: implement stopLocation
    throw UnimplementedError();
  }

  @override
  Future<bool> stopUpdatingHeading() {
    // TODO: implement stopUpdatingHeading
    throw UnimplementedError();
  }

  @override
  Stream<BaiduHeading> onHeaderChanged() {
    // TODO: implement onHeaderChanged
    throw UnimplementedError();
  }

  @override
  Stream<BaiduLocation> onLocationChanged() {
    // TODO: implement onLocationChanged
    throw UnimplementedError();
  }

}

void main() {
  final BmlocationPlatform initialPlatform = BmlocationPlatform.instance;

  test('$MethodChannelBmlocation is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelBmlocation>());
  });

  test('getPlatformVersion', () async {
    Bmlocation bmlocationPlugin = Bmlocation();
    MockBmlocationPlatform fakePlatform = MockBmlocationPlatform();
    BmlocationPlatform.instance = fakePlatform;

    expect(await bmlocationPlugin.getPlatformVersion(), '42');
  });
}
