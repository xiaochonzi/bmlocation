
import 'package:bmlocation/models/flutter_baidu_heading.dart';
import 'package:bmlocation/models/flutter_baidu_location.dart';

import 'bmlocation_platform_interface.dart';

class Bmlocation {
  Future<String?> getPlatformVersion() {
    return BmlocationPlatform.instance.getPlatformVersion();
  }

  // 设置AK(仅支持iOS)
  // Android 目前不支持接口设置Apikey,
  // 请在主工程的Manifest文件里设置，详细配置方法请参考官网(https://lbsyun.baidu.com/)
  Future<bool> authAK(String key) async {
    return BmlocationPlatform.instance.authAk(key);
  }

  // 设置是否同意隐私政策
  // 隐私政策官网链接：https://lbsyun.baidu.com/index.php?title=openprivacy
  // 未同意隐私政策之前无法使用定位及地理围栏等功能。
  Future<bool> setAgreePrivacy(bool isAgree) async {
    return BmlocationPlatform.instance.setAgreePrivacy(isAgree);
  }

  //设置定位参数
  Future<bool> prepareLoc(Map androidMap, Map iosMap) async {
    return BmlocationPlatform.instance.prepareLoc(androidMap, iosMap);
  }

  //开始定位
  Future<bool> startLocation() async {
    return BmlocationPlatform.instance.startLocation();
  }

  Future<bool> requestLocation() async {
    return BmlocationPlatform.instance.requestLocation();
  }

  Future<bool> restart() async {
    return BmlocationPlatform.instance.restart();
  }

  //停止定位
  Future<bool> stopLocation() async {
    return BmlocationPlatform.instance.stopLocation();
  }

  //单次定位（ios独有）
  Future<bool> singleLocation(Map arguments) async {
    return BmlocationPlatform.instance.singleLocation(arguments);
  }

  //返回设备是否支持设备朝向（仅iOS支持）
  Future<bool> headingAvailable() async {
    return BmlocationPlatform.instance.headingAvailable();
  }

  //获取设备朝向
  Future<bool> startUpdatingHeading() async {
    return BmlocationPlatform.instance.startUpdatingHeading();
  }

  //停止获取设备朝向
  Future<bool> stopUpdatingHeading() async {
    return BmlocationPlatform.instance.stopUpdatingHeading();
  }

  //设备定位回调
  Stream<BaiduLocation> onLocationChanged() {
    return BmlocationPlatform.instance.onLocationChanged();
  }
  //设备朝向回调
  Stream<BaiduHeading> onHeaderChanged() {
    return BmlocationPlatform.instance.onHeaderChanged();
  }
}
