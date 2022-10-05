import 'package:bmlocation/models/flutter_baidu_heading.dart';
import 'package:bmlocation/models/flutter_baidu_location.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'bmlocation_method_channel.dart';

abstract class BmlocationPlatform extends PlatformInterface {
  /// Constructs a BmlocationPlatform.
  BmlocationPlatform() : super(token: _token);

  static final Object _token = Object();

  static BmlocationPlatform _instance = MethodChannelBmlocation();

  /// The default instance of [BmlocationPlatform] to use.
  ///
  /// Defaults to [MethodChannelBmlocation].
  static BmlocationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [BmlocationPlatform] when
  /// they register themselves.
  static set instance(BmlocationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> authAk(String key);

  Future<bool> setAgreePrivacy(bool isAgree);

  Future<bool> prepareLoc(Map androidMap, Map iosMap);

  Future<bool> startLocation();

  Future<bool> stopLocation();

  Future<bool> singleLocation(Map arguments);

  Future<bool> headingAvailable();

  Future<bool> startUpdatingHeading();

  Future<bool> stopUpdatingHeading() ;

  Stream<BaiduLocation> onLocationChanged();

  Stream<BaiduHeading> onHeaderChanged();

  Future<bool> requestLocation();

  Future<bool> restart();
}
