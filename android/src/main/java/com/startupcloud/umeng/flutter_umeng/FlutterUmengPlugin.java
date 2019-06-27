package com.startupcloud.umeng.flutter_umeng;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.json.JSONObject;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterUmengPlugin */
public class FlutterUmengPlugin implements MethodCallHandler {
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_umeng");
    channel.setMethodCallHandler(new FlutterUmengPlugin(registrar));
  }

  private Registrar mRegistrar;

  public FlutterUmengPlugin(Registrar registrar) {
    mRegistrar = registrar;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if ("registerMsgHandler".equals(call.method)) {
      registerHandler(call, result);
    } else if ("registerUserAlias".equals(call.method)) {
      registerUserAlias(call, result);
    } else if ("getRegistrationId".equals(call.method)) {
      getRegistrationId(call, result);
    }
    else {
      result.notImplemented();
    }
  }

  /**
   * 在native层Application中注册sdk，获取deviceToken
   * @param context
   * @param appKey
   * @param appSecret
   * @param appChannel
   * @param type
   */
  public static void registerSdk(Context context, String appKey, String appSecret, String appChannel, int type) {
    if (TextUtils.isEmpty(appKey)) {
      Log.e(Consts.TAG, "Invalid appKey format.");
      return;
    }

    if (TextUtils.isEmpty(appSecret)) {
      Log.e(Consts.TAG, "Invalid appSecret format.");
      return;
    }

    if (TextUtils.isEmpty(appChannel)) {
      Log.i(Consts.TAG, "Empty appChannel, using default channel name [base]");
      appChannel = "base";
    }

    if (type <= 0) {
      Log.i(Consts.TAG, "Empty deviceType, using default deviceType DEVICE_TYPE_PHONE");
      type = UMConfigure.DEVICE_TYPE_PHONE;
    }

    UMConfigure.init(context, appKey, appChannel, type, appSecret);
    PushAgent agent = PushAgent.getInstance(context);
    agent.register(new IUmengRegisterCallback() {
      @Override
      public void onSuccess(String s) {
        Log.i(Consts.TAG, "Umeng register success.");
      }

      @Override
      public void onFailure(String s, String s1) {
        Log.e(Consts.TAG, "Umeng register error: " + s + " " + s1);
      }
    });

  }

  /**
   * 在flutter层注册消息
   * @param call
   * @param result
   */
  private void registerHandler(MethodCall call, Result result) {
    new EventChannel(mRegistrar.messenger(), "flutter_umeng_event").setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, final EventChannel.EventSink eventSink) {
        Log.i(Consts.TAG, "Umeng event channel onListened.");
        PushAgent.getInstance(mRegistrar.activity()).setMessageHandler(new FlutterUmengMessageHandler(new UmengMsgArrivedCallback() {
          @Override
          public void msgArrived(int eventType, int sourceType, final String msgJson) {
            eventSink.success(new MsgInfo(eventType, sourceType, msgJson).toString());
          }
        }));
        PushAgent.getInstance(mRegistrar.activity()).setNotificationClickHandler(new FlutterUmengNotificationClickHandler(new UmengMsgArrivedCallback() {
          @Override
          public void msgArrived(int eventType, int sourceType, String msgJson) {
            eventSink.success(new MsgInfo(eventType, sourceType, msgJson).toString());
          }
        }));
      }

      @Override
      public void onCancel(Object o) {
        Log.i(Consts.TAG, "Umeng event channel onCanceled.");
      }
    });
  }

  private void registerUserAlias(MethodCall call, Result result) {
    try {
      String displayId = call.argument("userAlias");
      String aliasType = call.argument("aliasType");
      PushAgent.getInstance(mRegistrar.activity()).addAlias(displayId, aliasType, new UTrack.ICallBack() {
        @Override
        public void onMessage(boolean b, String s) {
          Log.i(Consts.TAG, "addAlias: " + s);
        }
      });
      result.success(true);
    } catch (Exception e) {
      result.success(false);
    }
  }

  private void getRegistrationId(MethodCall call, Result result) {
    try {
      result.success(PushAgent.getInstance(mRegistrar.activity()).getRegistrationId());
    } catch (Exception e) {
      result.success("");
    }
  }

  private class MsgInfo {
    public int eventType;
    public int msgSourceType;
    public String msgJson;

    public MsgInfo(int eventType, int msgSourceType, String msgJson) {
      this.eventType = eventType;
      this.msgSourceType = msgSourceType;
      this.msgJson = msgJson;
    }

    @Override
    public String toString() {
      try {
        JSONObject object = new JSONObject();
        object.put("eventType", eventType);
        object.put("msgSourceType", msgSourceType);
        object.put("msgJson", msgJson);
        return object.toString();
      } catch (Exception e) {
        return "";
      }
    }
  }
}