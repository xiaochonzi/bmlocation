package com.zy.bmlocation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class LocationMethodHandler extends BDAbstractLocationListener implements MethodChannel.MethodCallHandler, SensorEventListener {

    private static final String TAG = "LocationMethodHandler";
    private Context context;
    private MethodChannel channel;
    private LocationClient locationClient;
    private SensorManager sensorManager;
    private Double lastX = 0.0;

    public LocationMethodHandler(Context context, MethodChannel channel) {
        this.context = context;
        this.channel = channel;
        try {
            locationClient = new LocationClient(context);
            //locationClient.registerLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        // 为系统的方向传感器注册监听器
    }

    public LocationClient getLocationClient() {
        if (this.locationClient == null) {
            try {
                this.locationClient = new LocationClient(this.context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.locationClient;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case Constants.MethodID.LOCATION_SETAGREEPRIVACY: {
                boolean isAgreePrivacy = (Boolean) call.arguments;
                LocationClient.setAgreePrivacy(isAgreePrivacy);
                sendReturnResult(true, result);
            }
            ;
            break;
            case Constants.MethodID.LOCATION_SETOPTIONS: {
                Log.d(TAG, "onMethodCall: LOCATION_SETOPTIONS");
                boolean ret = updateOption(this.getLocationClient(), (Map) call.arguments);
                sendReturnResult(ret, result);
            }
            ;
            break;
            case Constants.MethodID.LOCATION_STOPLOC: {
                if (this.getLocationClient() != null) {
                    Log.d(TAG, "onMethodCall: LOCATION_STOPLOC");
                    this.getLocationClient().stop();
                }
                sendReturnResult(true, result);
            }
            ;
            break;
            case Constants.MethodID.LOCATION_NERWORKSTATE: {

            }
            ;
            break;
            case Constants.MethodID.LOCATION_STARTHEADING: {
                if (sensorManager != null) {
                    Log.d(TAG, "onMethodCall: LOCATION_STARTHEADING");
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
                }
                sendReturnResult(true, result);
            }
            ;
            break;
            case Constants.MethodID.LOCATION_STOPHEADING: {
                if (sensorManager != null) {
                    Log.d(TAG, "onMethodCall: LOCATION_STOPHEADING");
                    sensorManager.unregisterListener(this);
                }
            }
            ;
            break;
            case Constants.MethodID.LOCATION_SERIESLOC: {
                if (this.getLocationClient() != null) {
                    Log.d(TAG, "onMethodCall: LOCATION_SERIESLOC");
                    this.locationClient.registerLocationListener(this);
                    this.getLocationClient().start();
                }
                sendReturnResult(true, result);
            }
            ;
            break;
            default:
                result.notImplemented();
        }

    }

    // 设置参数
    private boolean updateOption(LocationClient mLocationClient, Map arguments) {
        boolean ret = false;
        if (arguments != null) {
            LocationClientOption option = new LocationClientOption();
            // 可选，设置是否返回逆地理地址信息。默认是true
            if (arguments.containsKey("isNeedAddress") && arguments.get("isNeedAddress") != null) {
                if (((boolean) arguments.get("isNeedAddress"))) {
                    option.setIsNeedAddress(true);
                } else {
                    option.setIsNeedAddress(false);
                }
            }
            // 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            if (arguments.containsKey("locationMode") && arguments.get("locationMode") != null) {
                if (((int) arguments.get("locationMode")) == 0) {
                    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 高精度模式
                } else if (((int) arguments.get("locationMode")) == 1) {
                    option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors); // 仅设备模式
                } else if (((int) arguments.get("locationMode")) == 2) {
                    option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving); // 仅网络模式
                } else {
                    option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 高精度模式
                }
            }
            // 可选，设置场景定位参数，包括签到场景、运动场景、出行场景
            if (arguments.containsKey("locationPurpose") && arguments.get("locationPurpose") != null) {
                if (((int) arguments.get("locationPurpose")) == 0) {
                    option.setLocationPurpose(LocationClientOption.BDLocationPurpose.SignIn); // 签到场景
                } else if (((int) arguments.get("locationPurpose")) == 1) {
                    option.setLocationPurpose(LocationClientOption.BDLocationPurpose.Transport); // 运动场景
                } else if (((int) arguments.get("locationPurpose")) == 2) {
                    option.setLocationPurpose(LocationClientOption.BDLocationPurpose.Sport); // 出行场景
                }
            }
            // 可选，设置需要返回海拔高度信息
            if (arguments.containsKey("isNeedAltitude") && arguments.get("isNeedAltitude") != null) {
                if (((boolean) arguments.get("isNeedAltitude"))) {
                    option.setIsNeedAltitude(true);
                } else {
                    option.setIsNeedAltitude(false);
                }
            }
            // 可选，设置是否使用gps，默认false
            if (arguments.containsKey("openGps") && arguments.get("openGps") != null) {
                if (((boolean) arguments.get("openGps"))) {
                    option.setOpenGps(true);
                } else {
                    option.setOpenGps(false);
                }
            }
            // 可选，设置是否允许返回逆地理地址信息，默认是true
            if (arguments.containsKey("isNeedLocationDescribe") && arguments.get("isNeedLocationDescribe") != null) {
                if (((boolean) arguments.get("isNeedLocationDescribe"))) {
                    option.setIsNeedLocationDescribe(true);
                } else {
                    option.setIsNeedLocationDescribe(false);
                }
            }
            // 可选，设置发起定位请求的间隔，int类型，单位ms
            // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
            // 如果设置非0，需设置1000ms以上才有效
            if (arguments.containsKey("scanspan") && arguments.get("scanspan") != null) {
                option.setScanSpan((int) arguments.get("scanspan"));
            }
            // 可选，设置返回经纬度坐标类型，默认bd09ll
            if (arguments.containsKey("coorType") && arguments.get("coorType") != null) {
                option.setCoorType((String) arguments.get("coorType"));
            }
            if (arguments.containsKey("coordType") && arguments.get("coordType") != null) {
                if (((int) arguments.get("coordType")) == 0) {
                    option.setCoorType("gcj02");
                } else if (((int) arguments.get("coordType")) == 1) {
                    option.setCoorType("wgs84");
                } else if (((int) arguments.get("coordType")) == 2) {
                    option.setCoorType("bd09ll");
                } else {
                    option.setCoorType("gcj02");
                }
            }
            // 设置是否需要返回附近的poi列表
            if (arguments.containsKey("isNeedLocationPoiList") && arguments.get("isNeedLocationPoiList") != null) {
                if (((boolean) arguments.get("isNeedLocationPoiList"))) {
                    option.setIsNeedLocationPoiList(true);
                } else {
                    option.setIsNeedLocationPoiList(false);
                }
            }
            // 设置是否需要最新版本rgc数据
            if (arguments.containsKey("isNeedNewVersionRgc") && arguments.get("isNeedNewVersionRgc") != null) {
                if (((boolean) arguments.get("isNeedNewVersionRgc"))) {
                    option.setNeedNewVersionRgc(true);
                } else {
                    option.setNeedNewVersionRgc(false);
                }
            }
            option.setProdName("flutter");
            mLocationClient.setLocOption(option);
            ret = true;
        }
        return ret;
    }

    /**
     * location更新回调
     *
     * @param bdLocation
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (null == bdLocation) {
            sendResultCallback(Constants.MethodID.LOCATION_SERIESLOC, "bdLocation is null", -1);
            return;
        }
        Log.e(TAG, "onReceiveLocation: 定位结果返回" + bdLocation.toString());
        Map<String, Object> result = new LinkedHashMap<>();

        // 场景定位获取结果
//            if (locationClient.getLocOption().getLocationPurpose) {
//                result.put("latitude", bdLocation.getLatitude()); // 纬度
//                result.put("longitude", bdLocation.getLongitude()); // 经度
//                flutterResult.success(result);
//                return;
//            }

        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation
                || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation
                || bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
            result.put("callbackTime", formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
            result.put("locType", bdLocation.getLocType()); // 定位结果类型
            if (bdLocation.getTime() != null && !TextUtils.isEmpty(bdLocation.getTime())) {
                result.put("locTime", bdLocation.getTime()); // 定位成功时间
            }
            result.put("probability", bdLocation.getMockGpsProbability()); //作弊概率
            result.put("course", bdLocation.getDirection()); // 航向
            result.put("latitude", bdLocation.getLatitude()); // 纬度
            result.put("longitude", bdLocation.getLongitude()); // 经度
            result.put("speed", bdLocation.getSpeed()); // 速度
            if (bdLocation.hasAltitude()) {
                result.put("altitude", bdLocation.getAltitude()); // 高度
            }
            result.put("radius", Double.parseDouble(String.valueOf(bdLocation.getRadius()))); // 定位精度
            if (bdLocation.getCountry() != null && !TextUtils.isEmpty(bdLocation.getCountry())) {
                result.put("country", bdLocation.getCountry()); // 国家
            }
            if (bdLocation.getProvince() != null && !TextUtils.isEmpty(bdLocation.getProvince())) {
                result.put("province", bdLocation.getProvince()); // 省份
            }
            if (bdLocation.getCity() != null && !TextUtils.isEmpty(bdLocation.getCity())) {
                result.put("city", bdLocation.getCity()); // 城市
            }
            if (bdLocation.getDistrict() != null && !TextUtils.isEmpty(bdLocation.getDistrict())) {
                result.put("district", bdLocation.getDistrict()); // 区域
            }
            if (bdLocation.getTown() != null && !TextUtils.isEmpty(bdLocation.getTown())) {
                result.put("town", bdLocation.getTown()); // 城镇
            }
            if (bdLocation.getStreet() != null && !TextUtils.isEmpty(bdLocation.getStreet())) {
                result.put("street", bdLocation.getStreet()); // 街道
            }
            if (bdLocation.getAddrStr() != null && !TextUtils.isEmpty(bdLocation.getAddrStr())) {
                result.put("address", bdLocation.getAddrStr()); // 地址
            }
            if (bdLocation.getAdCode() != null && !TextUtils.isEmpty(bdLocation.getAdCode())) {
                result.put("adCode", bdLocation.getAdCode()); // 行政区划编码
            }
            if (bdLocation.getCityCode() != null && !TextUtils.isEmpty(bdLocation.getCityCode())) {
                result.put("cityCode", bdLocation.getCityCode()); // 城市编码
            }
            if (bdLocation.getStreetNumber() != null && !TextUtils.isEmpty(bdLocation.getStreetNumber())) {
                result.put("getStreetNumber", bdLocation.getStreetNumber()); // 街道编码
            }
            if (bdLocation.getLocationDescribe() != null && !TextUtils.isEmpty(bdLocation.getLocationDescribe())) {
                result.put("locationDetail", bdLocation.getLocationDescribe()); // 位置语义化描述
            }
            if (null != bdLocation.getPoiList() && !bdLocation.getPoiList().isEmpty()) {
                List<Poi> pois = bdLocation.getPoiList();
                List<Map> poiList = new LinkedList<>();
                for (int i = 0; i < pois.size(); i++) {
                    Map<String, String> poiMap = new LinkedHashMap<>();
                    Poi p = pois.get(i);
                    poiMap.put("tags", p.getTags());
                    poiMap.put("name", p.getName());
                    poiMap.put("addr", p.getAddr());
                    poiList.add(poiMap);
                }
                result.put("pois", poiList); // 周边poi信息
            }
            // 兼容旧版本poiList
            if (null != bdLocation.getPoiList() && !bdLocation.getPoiList().isEmpty()) {
                List<Poi> pois = bdLocation.getPoiList();
                StringBuilder stringBuilder = new StringBuilder();
                if (pois.size() == 1) {
                    stringBuilder.append(pois.get(0).getName()).append(",").append(pois.get(0).getTags())
                            .append(pois.get(0).getAddr());
                } else {
                    for (int i = 0; i < pois.size() - 1; i++) {
                        stringBuilder.append(pois.get(i).getName()).append(",").append(pois.get(i).getTags())
                                .append(pois.get(i).getAddr()).append("|");
                    }
                    stringBuilder.append(pois.get(pois.size() - 1).getName()).append(",")
                            .append(pois.get(pois.size() - 1).getTags())
                            .append(pois.get(pois.size() - 1).getAddr());
                }
                result.put("poiList", stringBuilder.toString()); // 周边poi信息
            }
            if (null != bdLocation.getPoiRegion()) {
                PoiRegion poi = bdLocation.getPoiRegion();
                Map regonMap = new LinkedHashMap<>();
                regonMap.put("tags", poi.getTags());
                regonMap.put("name", poi.getName());
                regonMap.put("directionDesc", poi.getDerectionDesc());
                result.put("poiRegion", regonMap); // 当前位置poi信息
            }
//          if (bdLocation.getFloor() != null) {
//            // 当前支持高精度室内定位
//            String buildingID = bdLocation.getBuildingID();// 百度内部建筑物ID
//            String buildingName = bdLocation.getBuildingName();// 百度内部建筑物缩写
//            String floor = bdLocation.getFloor();// 室内定位的楼层信息，如 f1,f2,b1,b2
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(buildingID).append("-").append(buildingName).append("-").append(floor);
//            result.put("indoor", stringBuilder.toString()); // 室内定位结果信息
//            // 开启室内定位模式（重复调用也没问题），开启后，定位SDK会融合各种定位信息（GPS,WI-FI，蓝牙，传感器等）连续平滑的输出定位结果；
//            mLocationClient.startIndoorMode();
//          } else {
//            mLocationClient.stopIndoorMode(); // 处于室外则关闭室内定位模式
//          }
            // android端实时检测位置变化，将位置结果发送到flutter端
            result.put("errorCode", bdLocation.getLocType());
            sendResultCallback(Constants.MethodID.LOCATION_SERIESLOC, result, 0);

        } else {
            // 定位结果错误码
            // 定位失败描述信息
            String des = bdLocation.getLocTypeDescription();
            result.put("errorInfo", des);
            result.put("errorCode", bdLocation.getLocType());
            // android端实时检测位置变化，将位置结果发送到flutter端
            sendResultCallback(Constants.MethodID.LOCATION_SERIESLOC,
                    result, bdLocation.getLocType());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[0];
        Map<String, Object> result = new LinkedHashMap<>();
        if (Math.abs(x - lastX) > 0.1) {
            result.put("trueHeading", x);
            result.put("timestamp", formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
            result.put("headingAccuracy", (sensorEvent.accuracy) + 0.0);
            sendResultCallback(Constants.MethodID.LOCATION_STARTHEADING, result, 0);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void sendReturnResult(final boolean ret, MethodChannel.Result mResult) {
        if (null == mResult) {
            return;
        }
        mResult.success(new HashMap<String, Boolean>() {
            {
                put(Constants.RESULT_KEY, ret);
            }
        });
    }

    public void sendResultCallback(String methodID, final Object value, final int errorCode) {
        if (null == channel) {
            return;
        }
        channel.invokeMethod(methodID, new HashMap<String, Object>() {
            {
                put(Constants.RESULT_KEY, value);
                put(Constants.ERROR_KEY, errorCode);
            }
        });
    }

    /**
     * 格式化时间
     *
     * @param time
     * @param strPattern
     * @return
     */
    private String formatUTC(long time, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            sdf.applyPattern(strPattern);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sdf == null ? "NULL" : sdf.format(time);
    }
}
