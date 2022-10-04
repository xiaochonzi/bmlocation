package com.zy.bmlocation;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Map;

import io.flutter.plugin.common.EventChannel;

public class BaiduLocationClientHandler extends BDAbstractLocationListener {

    private static final String TAG = BaiduLocationClientHandler.class.getName();
    private String mPluginKey;
    private Context context;
    private EventChannel.EventSink mEventSink;
    private LocationClient locationClient;
    private LocationClientOption clientOption = new LocationClientOption();


    public BaiduLocationClientHandler(String mPluginKey, Context context, EventChannel.EventSink mEventSink) {
        this.mPluginKey = mPluginKey;
        this.context = context;
        this.mEventSink = mEventSink;
        try {
            locationClient = new LocationClient(context);
            locationClient.registerLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startLocation() {
        if (null == locationClient) {
            try {
                locationClient = new LocationClient(context);
                locationClient.registerLocationListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != clientOption) {
            locationClient.setLocOption(clientOption);
        }
        Log.d(TAG, "startLocation");
        locationClient.start();
    }

    public void updateOption(Map<String, Object> arguments) {
        LocationClientOption option = Common.convertToOption(arguments);
        this.clientOption = option;
    }

    public void stopLocation() {
        if (locationClient != null) {
            Log.d(TAG, "stopLocation");
            locationClient.unRegisterLocationListener(this);
            locationClient.stop();
        }
    }

    public void destroy() {
        locationClient = null;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.e(TAG, "onReceiveLocation: 定位结果返回" + bdLocation.toString());
        if (null == mEventSink) {
            return;
        }
        Map<String, Object> result = Common.convertToMap(bdLocation);
        result.put("pluginKey", mPluginKey);
        mEventSink.success(result);
    }

    @Override
    public void onReceiveLocString(String s) {
        Log.e(TAG, "onReceiveLocString: 结果返回" + s);
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {
        Log.e(TAG, "onConnectHotSpotMessage: 结果返回" + s + i);
    }
}
