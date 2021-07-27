/// Run ussd code directly in your application
import 'dart:async';

import 'package:flutter/services.dart';

class UssdAdvanced {
  static const MethodChannel _channel =
      MethodChannel('plugins.elyudde.com/ussd_advanced');

  static Future<void> sendUssd(
      {required String code, int subscriptionId = 1}) async {
    await _channel.invokeMethod(
        'sendUssd', {"subscriptionId": subscriptionId, "code": code});
  }

  static Future<String?> sendAdvancedUssd(
      {required String code, int subscriptionId = 1}) async {
    final String? response = await _channel
        .invokeMethod('sendAdvancedUssd',
            {"subscriptionId": subscriptionId, "code": code})
        .timeout(const Duration(seconds: 30))
        .catchError((e) {
          throw e;
        });
    return response;
  }
}
