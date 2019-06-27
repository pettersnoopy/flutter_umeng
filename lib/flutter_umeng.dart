import 'dart:async';

import 'package:flutter/services.dart';

class FlutterUmeng {
  static const MethodChannel _channel = const MethodChannel('flutter_umeng');
  static const EventChannel _eventChannel =
      const EventChannel('flutter_umeng_event');
  static StreamSubscription _streamSubscription;

  // static Future<String> initSdk(String appKey, String appSecret,
  //     {String appChannel = "base", int deviceType = 1}) async {
  //   return await _channel.invokeMethod('initSdk', {
  //     "appKey": appKey,
  //     "appSecret": appSecret,
  //     "appChannel": appChannel,
  //     "deviceType": deviceType,
  //   });
  // }

  static void registerUmengListener(Function listener) {
    _channel.invokeMethod("registerMsgHandler");
    if (_streamSubscription == null) {
      _streamSubscription =
          _eventChannel.receiveBroadcastStream().listen(listener);
    }
  }

  static void unregisterUmengListener() {
    if (_streamSubscription != null) {
      _streamSubscription.cancel();
      _streamSubscription = null;
    }
  }

  static Future<bool> registerUserAlias(
      String userAlias, String aliasType) async {
    return await _channel.invokeMethod('registerUserAlias', {
      "userAlias": userAlias,
      "aliasType": aliasType,
    });
  }

  static Future<String> getRegistrationId() async {
    return await _channel.invokeMethod('getRegistrationId');
  }

  static Future<String> getCachedNotificationMsg() async {
    return await _channel.invokeMethod('getCachedNotificationMsg');
  }

  static Future<bool> clearCachedNotificationMsg() async {
    return await _channel.invokeMethod('clearCachedNotificationMsg');
  }
}
