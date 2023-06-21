#import "UssdAdvancedPlugin.h"

@implementation UssdAdvancedPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"method.com.phan_tech/ussd_advanced"
            binaryMessenger:[registrar messenger]];
  UssdAdvancedPlugin* instance = [[UssdAdvancedPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"sendUssd" isEqualToString:call.method]) {
    NSString* number = call.arguments[@"code"];
    result(@([self directCall:number]));
  }else if ([@"sendAdvancedUssd" isEqualToString:call.method]) {
    NSString* number = call.arguments[@"code"];
    result(@([self directCall:number]));
  }else if ([@"multisessionUssd" isEqualToString:call.method]) {
    NSString* number = call.arguments[@"code"];
    result(@([self directCall:number]));
  } else {
    result(FlutterMethodNotImplemented);
  }
}

  - (BOOL)directCall:(NSString*)number {
    number = [number stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    if( ! [number hasPrefix:@"tel:"]){
        number =  [NSString stringWithFormat:@"tel:%@", number];
    }
    if(![[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:number]]) {
        return NO;
    } else if(![[UIApplication sharedApplication] openURL:[NSURL URLWithString:number]]) {
        // missing phone number
        return NO;
    } else {
        return YES;
    }
}

@end
