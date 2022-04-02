# ussd_advanced

Run ussd code directly in your application

## Usage

Add dependency to pubspec.yaml file


### Android
You'll need to add the `CALL_PHONE` permission and `READ_PHONE_STATE` to your Android Manifest.
```XML
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
### For android mutli-session ussd
Add ussd dialog accessibility service to your Android Manifet inside application
```XML
<application>
...
  <service
      android:name="com.phan_tech.ussd_advanced.USSDServiceKT"
      android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
      android:exported="false">
      <intent-filter>
          <action android:name="android.accessibilityservice.AccessibilityService" />
      </intent-filter>
      <meta-data
          android:name="android.accessibilityservice"
          android:resource="@xml/ussd_service" />
  </service>
</application>
```

### iOS
Add this to your ```info.plist``` under ```dict```
```
<key>LSApplicationQueriesSchemes</key>
<array>
  <string>tel</string>
</array>
```

### Example
```dart
import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:ussd_advanced/ussd_advanced.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late TextEditingController _controller;
  String? _response;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Ussd Plugin example'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          children: [
            // text input
            TextField(
              controller: _controller,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(labelText: 'Ussd code'),
            ),

            // dispaly responce if any
            if (_response != null)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Text(_response!),
              ),

            // buttons
            Row(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: () {
                    UssdAdvanced.sendUssd(code: _controller.text, subscriptionId: 1);
                  },
                  child: const Text('norma\nrequest'),
                ),
                ElevatedButton(
                  onPressed: () async {
                    String? _res = await UssdAdvanced.sendAdvancedUssd(code: _controller.text, subscriptionId: 1);
                    setState(() {
                      _response = _res;
                    });
                  },
                  child: const Text('single session\nrequest'),
                ),
                ElevatedButton(
                  onPressed: () async {
                    String? _res = await UssdAdvanced.multisessionUssd(code: _controller.text, subscriptionId: 1);
                    setState(() {
                      _response = _res;
                    });
                    String? _res2 = await UssdAdvanced.sendMessage('0');
                    setState(() {
                      _response = _res2;
                    });
                    await UssdAdvanced.cancelSession();
                  },
                  child: const Text('multi session\nrequest'),
                ),
              ],
            )
          ],
        ),
      ),
    );
  }
}

```

### Methods

- The `sendUssd` runs the ussd normaly without a response.
- The `sendAdvancedUssd` (single session call) runs ussd in background and gives you the response as a string. It only surports android 8+(SDK 26+). It defaults to `sendUssd` when the SDK is lower. For multisession USSDs, send the request as one, ie;
```dart
UssdAdvanced.sendAdvancedUssd(code: "*123*1*4*3#", subscriptionId: -1);
```

- The `multisessionUssd` runs a multi session call giving all the sessions response as String. One can interact with the ussd dialog by sending a message using `sendMessage` method and canceling the session by calling `cancelSession`

### Selecting SimCard
You can select which simcard to use providing `subscriptionId`.
-1: is for default phone setting.
Only supports android 6+, and defaults to default if the SDK is lower

Support the plugin <a href="https://www.buymeacoffee.com/EddieGenius" target="_blank"><img src="https://i.imgur.com/aV6DDA7.png" alt="Buy Me A Coffee" style="height: 41px !important;width: 174px !important; box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;-webkit-box-shadow: 0px 3px 2px 0px rgba(190, 190, 190, 0.5) !important;" > </a>
