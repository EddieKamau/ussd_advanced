import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ussd_advanced/ussd_advanced.dart';

void main() {
  const MethodChannel channel =
      MethodChannel('method.com.phan_tech/ussd_advanced');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return 'Your account is';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(
        await UssdAdvanced.sendAdvancedUssd(code: "*100#"), 'Your account is');
  });
}
