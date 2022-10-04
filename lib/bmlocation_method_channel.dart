import 'dart:async';
import 'dart:io';

import 'package:bmlocation/models/flutter_baidu_heading.dart';
import 'package:bmlocation/models/flutter_baidu_location.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'bmlocation_platform_interface.dart';
import 'consts.dart';

/// An implementation of [BmlocationPlatform] that uses method channels.
class MethodChannelBmlocation extends BmlocationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  static const MethodChannel channel = const MethodChannel(BMFLocationConstants.kLocationChannelName);
  static const EventChannel locationEvent = const EventChannel(BMFLocationConstants.kLocationStreamName);

  static Stream<Map<String, Object>> _onLocationChanged = locationEvent
      .receiveBroadcastStream()
      .asBroadcastStream()
      .map<Map<String, Object>>((element) => element.cast<String, Object>());

  StreamController<BaiduLocation>? _receiveStream;
  StreamSubscription<Map<String, Object>>? _subscription;

  String? _pluginKey;

  MethodChannelBmlocation(){
    _pluginKey = DateTime.now().millisecondsSinceEpoch.toString();
  }

  @override
  Future<String?> getPlatformVersion() async {
    final version = await channel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> authAk(String key)  async {
    bool result = false;
    try {
      Map map = (await channel.invokeMethod(BMFLocationAuthMethodId.kLocationSetApiKey, key) as Map);
      result = map['result'] as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  @override
  Future<bool> headingAvailable() async {
    bool result = false;
    print(BMFLocationAuthMethodId.kLocationSetApiKey);
    try {
      Map map = (await channel.invokeMethod(BMFLocationHeadingMethodId.kLocationHeadingAvailable) as Map);
    result = map['result'] as bool;
    } on PlatformException catch (e) {
    print(e.toString());
    }
    return result;
  }


  @override
  Future<bool> prepareLoc(Map androidMap, Map iosMap) async {
    bool result = false;
    try {
      Map args = Platform.isAndroid ? androidMap : iosMap;
      args['pluginKey'] = _pluginKey;
      Map map = (await channel.invokeMethod(BMFLocationOptionsMethodId.kLocationSetOptions, args) as Map);
    result = map['result'] as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  @override
  Future<bool> setAgreePrivacy(bool isAgree) async {
    bool result = false;
    try {
      Map map = (await channel.invokeMethod(
          BMFLocationAuthMethodId.kLocationSetAgreePrivacy, isAgree) as Map);
    result = map['result'] as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }


  @override
  Future<bool> singleLocation(Map arguments) async {
    bool result = false;
    try {
      Map map = (await channel
          .invokeMethod(BMFLocationResultMethodId.kLocationSingleLocation, {
        'pluginKey': _pluginKey,
        'isReGeocode': arguments['isReGeocode'],
        'isNetworkState': arguments['isNetworkState']
      }) as Map);
      result = map['result'] as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  @override
  Future<bool> startLocation() async {
    bool result = false;
    try {
      Map map = (await channel.invokeMethod(
          BMFLocationResultMethodId.kLocationSeriesLocation, {'pluginKey': _pluginKey}) as Map);
      result = map['result'] as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  @override
  Future<bool> startUpdatingHeading() async {
    bool result = false;
    try {
      Map map = (await channel.invokeMethod(
          BMFLocationHeadingMethodId.kLocationStartHeading, {'pluginKey': _pluginKey}) as Map);
    result = map['result'] as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  @override
  Future<bool> stopLocation() async {
    bool result = false;
    try {
      Map map = (await channel.invokeMethod(
          BMFLocationResultMethodId.kLocationStopLocation, {'pluginKey': _pluginKey}) as Map);
    result = map['result'] as bool;
    } on PlatformException catch (e) {
    print(e.toString());
    }
    return result;
  }

  @override
  Future<bool> stopUpdatingHeading() async {
    bool result = false;
    try {
      Map map = (await channel.invokeMethod(
          BMFLocationHeadingMethodId.kLocationStopHeading, {'pluginKey': _pluginKey}) as Map);
    result = map['result'] as bool;
    } on PlatformException catch (e) {
    print(e.toString());
    }
    return result;
  }

  void destroy() {
    if (_subscription != null) {
      _receiveStream?.close();
      _subscription?.cancel();
      _receiveStream = null;
      _subscription = null;
    }
  }

  @override
  Stream<BaiduHeading> onHeaderChanged() {
    // TODO: implement onHeaderChanged
    throw UnimplementedError();
  }

  @override
  Stream<BaiduLocation> onLocationChanged() {
    if (_receiveStream == null) {
      _receiveStream = StreamController();
      _subscription = _onLocationChanged.listen((Map<String, Object> event) {
        if (event != null && event['pluginKey'] == _pluginKey) {
          BaiduLocation location = BaiduLocation.fromMap(event);
          _receiveStream?.add(location);
        }
      });
    }
    return _receiveStream!.stream;
  }
}
