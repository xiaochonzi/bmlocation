import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:bmlocation/bmlocation_method_channel.dart';

void main() {
  MethodChannelBmlocation platform = MethodChannelBmlocation();
  const MethodChannel channel = MethodChannel('bmlocation');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
