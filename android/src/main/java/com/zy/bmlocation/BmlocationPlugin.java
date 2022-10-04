package com.zy.bmlocation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.location.LocationClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * BmlocationPlugin
 */
public class BmlocationPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
    private static final String TAG = BmlocationPlugin.class.getName();
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;
    public EventChannel.EventSink mEventSink = null;
    private Context context;

    private Map<String, BaiduLocationClientHandler> locationClientMap = new ConcurrentHashMap<String, BaiduLocationClientHandler>(8);

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), Constants.MethodChannelName.LOCATION_CHANNEL);
        channel.setMethodCallHandler(this);
        final EventChannel eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), Constants.MethodChannelName.LOCATION_STREAM);
        eventChannel.setStreamHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        for (Map.Entry<String, BaiduLocationClientHandler> entry : locationClientMap.entrySet()) {
            entry.getValue().destroy();
        }
        channel.setMethodCallHandler(null);
        channel = null;

    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        this.mEventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        for (Map.Entry<String, BaiduLocationClientHandler> entry : locationClientMap.entrySet()) {
            entry.getValue().destroy();
        }
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case Constants.MethodID.LOCATION_SETAGREEPRIVACY: {
                Log.d(TAG, "onMethodCall:LOCATION_SETAGREEPRIVACY");
                boolean isAgreePrivacy = (Boolean) call.arguments;
                LocationClient.setAgreePrivacy(isAgreePrivacy);
                sendReturnResult(true, result);
            }
            break;
            case Constants.MethodID.LOCATION_SETOPTIONS: {
                Log.d(TAG, "onMethodCall:LOCATION_SETOPTIONS");
                Map<String, Object> argsMap = (Map<String, Object>) call.arguments;
                BaiduLocationClientHandler handler = getHandler(argsMap);
                handler.updateOption(argsMap);
                sendReturnResult(true, result);
            }
            break;
            case Constants.MethodID.LOCATION_STOPLOC: {
                Log.d(TAG, "onMethodCall:LOCATION_STOPLOC");
                Map<String, Object> argsMap = (Map<String, Object>) call.arguments;
                BaiduLocationClientHandler handler = getHandler(argsMap);
                handler.stopLocation();
                sendReturnResult(true, result);
            }
            break;
            case Constants.MethodID.LOCATION_NERWORKSTATE: {
            }
            break;
            case Constants.MethodID.LOCATION_STARTHEADING: {
                Log.d(TAG, "onMethodCall:LOCATION_STARTHEADING");
                sendReturnResult(true, result);
            }
            break;
            case Constants.MethodID.LOCATION_STOPHEADING: {
                Log.d(TAG, "onMethodCall:LOCATION_STOPHEADING");
            }
            break;
            case Constants.MethodID.LOCATION_SERIESLOC: {
                Log.d(TAG, "onMethodCall:LOCATION_SERIESLOC");
                Map<String, Object> argsMap = (Map<String, Object>) call.arguments;
                BaiduLocationClientHandler handler = getHandler(argsMap);
                handler.startLocation();
                sendReturnResult(true, result);
            }
            break;
            default:
                result.notImplemented();
        }
    }

    private BaiduLocationClientHandler getHandler(Map<String, Object> argsMap) {
        if (null == locationClientMap) {
            locationClientMap = new ConcurrentHashMap<String, BaiduLocationClientHandler>(8);
        }
        String pluginKey = getPluginKeyFromArgs(argsMap);
        Log.d(TAG, "getLocationClientImp: " + pluginKey);
        if (TextUtils.isEmpty(pluginKey)) {
            return null;
        }
        if (!locationClientMap.containsKey(pluginKey)) {
            BaiduLocationClientHandler locationClientImp = new BaiduLocationClientHandler(pluginKey, context, mEventSink);
            locationClientMap.put(pluginKey, locationClientImp);
        }
        return locationClientMap.get(pluginKey);
    }

    private String getPluginKeyFromArgs(Map<String, Object> argsMap) {
        String pluginKey = null;
        try {
            if (null != argsMap) {
                pluginKey = (String) argsMap.get("pluginKey");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return pluginKey;
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
}
