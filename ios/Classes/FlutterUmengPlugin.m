#import "FlutterUmengPlugin.h"
#include <UMPush/UMessage.h>
@implementation FlutterUmengPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_umeng"
            binaryMessenger:[registrar messenger]];
  FlutterUmengPlugin* instance = [[FlutterUmengPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
 if ([@"getRegistrationId" isEqualToString:call.method]) {
        NSString *deviceToken = [[NSUserDefaults standardUserDefaults] objectForKey:@"UmengDeviceToken"];
        if (deviceToken != nil && ![deviceToken isEqualToString:@""] && deviceToken.length > 0){
            result(deviceToken);
        }
    } else if ([@"getCachedNotificationMsg" isEqualToString:call.method]){
        NSString *userInfo = [[NSUserDefaults standardUserDefaults] objectForKey:@"PushUserInfoKey"];
        if (userInfo != nil && ![userInfo isEqualToString:@""] && userInfo.length > 0){
            result(userInfo);
        }
    } else if([@"clearCachedNotificationMsg" isEqualToString:call.method]) {
        [[NSUserDefaults standardUserDefaults]removeObjectForKey:@"PushUserInfoKey"];
        result(@(YES));
    } else if([@"registerUserAlias" isEqualToString:call.method]) {
        NSString *userAlias = call.arguments[@"userAlias"];
        NSString *aliasType = call.arguments[@"aliasType"];
        //绑定别名
        [UMessage addAlias:userAlias type:aliasType response:^(id  _Nonnull responseObject, NSError * _Nonnull error) {
            if (error != nil) {
                result(@(NO));
            }
            result(@(YES));
        }];
    } else  {
        result(FlutterMethodNotImplemented);
    }
}

@end
