#import "FlutterUmengPlugin.h"
#import <UMCommon/UMCommon.h>
@implementation FlutterUmengPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_umeng"
            binaryMessenger:[registrar messenger]];
  FlutterUmengPlugin* instance = [[FlutterUmengPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"init" isEqualToString:call.method]) {
        [UMConfigure initWithAppkey:@"5d1487e2570df39b6b0006d9" channel:nil];
    }
  else if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
