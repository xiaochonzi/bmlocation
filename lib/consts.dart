class BMFLocationConstants {
  static const kLocationChannelName = 'bmlocation_channel';
  static const kLocationStreamName = 'bmlocation_location_stream';
  static const kHeaderStreamName = 'bmlocation_header_stream';
}

class BMFLocationAuthMethodId {
  /// 设置ak进行鉴权
  static const kLocationSetApiKey =
      BMFLocationConstants.kLocationChannelName + '/setAK';

  /// 设置是否同意隐私政策
  static const kLocationSetAgreePrivacy =
      BMFLocationConstants.kLocationChannelName + '/setAgreePrivacy';
}

class BMFLocationOptionsMethodId {
  ///设置参数
  static const kLocationSetOptions =
      BMFLocationConstants.kLocationChannelName + '/setOptions';
}

class BMFLocationResultMethodId {
  ///单次定位
  static const kLocationSingleLocation =
      BMFLocationConstants.kLocationChannelName + '/singleLocation';

  ///连续定位
  static const kLocationSeriesLocation =
      BMFLocationConstants.kLocationChannelName + '/seriesLocation';

  ///停止定位
  static const kLocationStopLocation =
      BMFLocationConstants.kLocationChannelName + '/stopLocation';
}

///仅支持iOS
class BMFLocationHeadingMethodId {
  ///是否支持设备朝向
  static const kLocationHeadingAvailable =
      BMFLocationConstants.kLocationChannelName + '/headingAvailable';

  ///开启设备朝向
  static const kLocationStartHeading =
      BMFLocationConstants.kLocationChannelName + '/startUpdatingHeading';

  ///停止设备朝向
  static const kLocationStopHeading =
      BMFLocationConstants.kLocationChannelName + '/stopUpdatingHeading';
}