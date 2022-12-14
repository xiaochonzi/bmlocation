import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:bmlocation/bmlocation.dart';
import 'package:bmlocation/models/flutter_baidu_location.dart';
import 'package:bmlocation/models/flutter_baidu_location_android_option.dart';
import 'package:bmlocation/models/flutter_baidu_location_base_option.dart';
import 'package:bmlocation/models/flutter_baidu_location_ios_option.dart';
import 'package:flutter/material.dart';
import 'package:flutter_background_service/flutter_background_service.dart';
import 'package:flutter_background_service_android/flutter_background_service_android.dart';
import 'package:flutter_baidu_mapapi_base/flutter_baidu_mapapi_base.dart'
    show BMFMapSDK, BMF_COORD_TYPE;
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:permission_handler/permission_handler.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  initializeService();
  runApp(const MyApp());
}

Future<void> initializeService() async {
  final service = FlutterBackgroundService();
  /// OPTIONAL, using custom notification channel id
  const AndroidNotificationChannel channel = AndroidNotificationChannel(
    'my_foreground', // id
    'MY FOREGROUND SERVICE', // title
    description:
    'This channel is used for important notifications.', // description
    importance: Importance.low, // importance must be at low or higher level
  );

  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  FlutterLocalNotificationsPlugin();

  if (Platform.isIOS) {
    await flutterLocalNotificationsPlugin.initialize(
      const InitializationSettings(
        iOS: IOSInitializationSettings(),
      ),
    );
  }
  await flutterLocalNotificationsPlugin.resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()?.createNotificationChannel(channel);
  await service.configure(
    androidConfiguration: AndroidConfiguration(
      // this will be executed when app is in foreground or background in separated isolate
      onStart: onStart,
      // auto start service
      autoStart: false,
      isForegroundMode: true,
      notificationChannelId: 'my_foreground',
      initialNotificationTitle: 'AWESOME SERVICE',
      initialNotificationContent: 'Initializing',
      foregroundServiceNotificationId: 888,
    ),
    iosConfiguration: IosConfiguration(
      // auto start service
      autoStart: false,
      // this will be executed when app is in foreground in separated isolate
      onForeground: onStart,
      // you have to enable background fetch capability on xcode project
      onBackground: onIosBackground,
    ),
  );
  service.startService();
}

@pragma('vm:entry-point')
Future<bool> onIosBackground(ServiceInstance service) async {
  WidgetsFlutterBinding.ensureInitialized();
  DartPluginRegistrant.ensureInitialized();
  return true;
}

/// ??????????????????
BaiduLocationAndroidOption _initAndroidOptions() {
  BaiduLocationAndroidOption options = BaiduLocationAndroidOption(
      locationMode: BMFLocationMode.hightAccuracy,
      isNeedAddress: true,
      isNeedAltitude: true,
      isNeedLocationPoiList: true,
      isNeedNewVersionRgc: true,
      isNeedLocationDescribe: true,
      openGps: true,
      scanspan: 1000,
      coordType: BMFLocationCoordType.bd09ll);
  return options;
}

BaiduLocationIOSOption _initIOSOptions() {
  BaiduLocationIOSOption options = BaiduLocationIOSOption(
      coordType: BMFLocationCoordType.bd09ll,
      desiredAccuracy: BMFDesiredAccuracy.best,
      allowsBackgroundLocationUpdates: true,
      pausesLocationUpdatesAutomatically: false);
  return options;
}

@pragma('vm:entry-point')
void onStart(ServiceInstance service) async {
  DartPluginRegistrant.ensureInitialized();
  Bmlocation myLocPlugin = Bmlocation();
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
  if (service is AndroidServiceInstance) {
    service.on('setAsForeground').listen((event) {
      service.setAsForegroundService();
    });
    service.on('setAsBackground').listen((event) {
      service.setAsBackgroundService();
    });
  }
  service.on('stopService').listen((event) {
    service.stopSelf();
    myLocPlugin.stopLocation();
  });
  print("dddd");
  myLocPlugin.setAgreePrivacy(true);
  myLocPlugin.onLocationChanged().listen((event) {
    print(event.getMap());
    service.invoke("update", {"location":event.getMap()});
  });

  Map iosMap = _initIOSOptions().getMap();
  Map androidMap = _initAndroidOptions().getMap();
  myLocPlugin.prepareLoc(androidMap, iosMap);
  Timer.periodic(Duration(seconds: 10), (timer) {
    myLocPlugin.startLocation();
  });
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  BaiduLocation _loationResult = BaiduLocation();
  Bmlocation myLocPlugin = Bmlocation();
  bool _suc = false;
  @override
  void initState() {
    super.initState();
    /// ????????????????????????
    requestPermission();
    // ????????????????????????
    myLocPlugin.setAgreePrivacy(true);
    BMFMapSDK.setAgreePrivacy(true);
    if (Platform.isIOS) {
      /// ??????ios???ak, android???ak????????????????????????????????????
      myLocPlugin.authAK('??? ??? ??? ??? ??? AK');
      BMFMapSDK.setApiKeyAndCoordType('??? ??? ??? ??? ??? AK', BMF_COORD_TYPE.BD09LL);
    } else if (Platform.isAndroid) {
      // Android ???????????????????????????Apikey,
      // ??????????????????Manifest???????????????????????????????????????????????????(https://lbsyun.baidu.com/)demo
      BMFMapSDK.setCoordType(BMF_COORD_TYPE.BD09LL);
    }
    ///??????????????????
  }

  String text = "Stop Service";
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          StreamBuilder<Map<String, dynamic>?>(
            stream: FlutterBackgroundService().on('update'),
            builder: (context, snapshot) {
              if (!snapshot.hasData) {
                return const Center(
                  child: CircularProgressIndicator(),
                );
              }
              final data = snapshot.data!;
              Map location = data["location"];
              List<Widget> resultWidgets = [];
              if (location['locTime'] != null) {
                location.forEach((key, value) {
                  resultWidgets.add(_resultWidget(key, value));
                });
              }
              return Column(
                children: [
                  SizedBox(
                    height: MediaQuery.of(context).size.height - 500,
                    child: ListView(
                      children: resultWidgets,
                    ),
                  ),
                ],
              );
            },
          ),
          ElevatedButton(
            child: const Text("Foreground Mode"),
            onPressed: () {
              FlutterBackgroundService().invoke("setAsForeground");
            },
          ),
          ElevatedButton(
            child: const Text("Background Mode"),
            onPressed: () {
              FlutterBackgroundService().invoke("setAsBackground");
            },
          ),
          ElevatedButton(
            child: Text(text),
            onPressed: () async {
              final service = FlutterBackgroundService();
              var isRunning = await service.isRunning();
              if (isRunning) {
                service.invoke("stopService");
              } else {
                service.startService();
              }
              if (!isRunning) {
                text = 'Stop Service';
              } else {
                text = 'Start Service';
              }
              setState(() {});
            },
          ),
        ],
      ),
    );
  }

  Widget _resultWidget(key, value) {
    return Center(
      child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text('$key:' ' $value'),
          ]),
    );
  }

  Container _createButtonContainer() {
    return Container(
        alignment: Alignment.center,
        child: Row(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
                onPressed: () {
                  ///??????????????????
                  _locationAction();
                  _startLocation();
                },
                child: const Text('????????????'),
                style: ElevatedButton.styleFrom(
                  primary:
                  Colors.blueAccent, //change background color of button
                  onPrimary: Colors.yellow, //change text color of button
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(5),
                  ),
                )),
            Container(width: 20),
            ElevatedButton(
                onPressed: () {
                  _stopLocation();
                },
                child: const Text('????????????'),
                style: ElevatedButton.styleFrom(
                  primary:
                  Colors.blueAccent, //change background color of button
                  onPrimary: Colors.yellow, //change text color of button
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(5),
                  ),
                ))
          ],
        ));
  }

  void _locationAction() async {
    /// ??????android??????ios???????????????
    /// android ?????????????????????
    /// ios ?????????????????????
    Map iosMap = _initIOSOptions().getMap();
    Map androidMap = _initAndroidOptions().getMap();

    _suc = await myLocPlugin.prepareLoc(androidMap, iosMap);
    print('?????????????????????$iosMap');
  }

  /// ??????????????????
  BaiduLocationAndroidOption _initAndroidOptions() {
    BaiduLocationAndroidOption options = BaiduLocationAndroidOption(
        locationMode: BMFLocationMode.hightAccuracy,
        isNeedAddress: true,
        isNeedAltitude: true,
        isNeedLocationPoiList: true,
        isNeedNewVersionRgc: true,
        isNeedLocationDescribe: true,
        openGps: true,
        scanspan: 4000,
        coordType: BMFLocationCoordType.bd09ll);
    return options;
  }

  BaiduLocationIOSOption _initIOSOptions() {
    BaiduLocationIOSOption options = BaiduLocationIOSOption(
        coordType: BMFLocationCoordType.bd09ll,
        desiredAccuracy: BMFDesiredAccuracy.best,
        allowsBackgroundLocationUpdates: true,
        pausesLocationUpdatesAutomatically: false);
    return options;
  }

  // /// ????????????
  Future<void> _startLocation() async {
    _suc = await myLocPlugin.startLocation();
    print('?????????????????????$_suc');
  }

  /// ????????????
  void _stopLocation() async {
    _suc = await myLocPlugin.stopLocation();
    print('?????????????????????$_suc');
  }

  // ????????????????????????
  void requestPermission() async {
    // ????????????
    bool hasLocationPermission = await requestLocationPermission();
    if (hasLocationPermission) {
      // ??????????????????
    } else {}
  }

  /// ??????????????????
  /// ????????????????????????true??? ????????????false
  Future<bool> requestLocationPermission() async {
    //?????????????????????
    var status = await Permission.locationAlways.status;
    if (status == PermissionStatus.granted) {
      //????????????
      return true;
    } else {
      //??????????????????????????????
      status = await Permission.locationAlways.request();
      if (status == PermissionStatus.granted) {
        return true;
      } else {
        return false;
      }
    }
  }
}
