package com.startupcloud.umeng.flutter_umeng;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
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
    } else if ("getCachedNotificationMsg".equals(call.method)) {
      getCachedNotificationMsg(call, result);
    } else if ("clearCachedNotificationMsg".equals(call.method)) {
      clearCachedNotificationMsg(call, result);
    } else {
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
  public static void registerSdk(Application context, String appKey, String appSecret, String appChannel, int type) {
    if (TextUtils.isEmpty(appKey)) {
      LogUtils.e(Consts.TAG, "Invalid appKey format.");
      return;
    }

    if (TextUtils.isEmpty(appSecret)) {
      LogUtils.e(Consts.TAG, "Invalid appSecret format.");
      return;
    }

    if (TextUtils.isEmpty(appChannel)) {
      LogUtils.i(Consts.TAG, "Empty appChannel, using default channel name [base]");
      appChannel = "base";
    }

    if (type <= 0) {
      LogUtils.i(Consts.TAG, "Empty deviceType, using default deviceType DEVICE_TYPE_PHONE");
      type = UMConfigure.DEVICE_TYPE_PHONE;
    }

    UMConfigure.init(context, appKey, appChannel, type, appSecret);
    PushAgent agent = PushAgent.getInstance(context);
    agent.register(new IUmengRegisterCallback() {
      @Override
      public void onSuccess(String s) {
        LogUtils.i(Consts.TAG, "Umeng register success.");
      }

      @Override
      public void onFailure(String s, String s1) {
        LogUtils.e(Consts.TAG, "Umeng register error: " + s + " " + s1);
      }
    });
  }

  public static void registerHuaWei(Application application) {
    HuaWeiRegister.register(application);
  }

  public static void registerMiPush(Context context, String appId, String appKey) {
    MiPushRegistar.register(context, appId, appKey);
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
        LogUtils.i(Consts.TAG, "Umeng event channel onListened.");
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
        LogUtils.i(Consts.TAG, "Umeng event channel onCanceled.");
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
          LogUtils.i(Consts.TAG, "addAlias: " + s);
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

  private void getCachedNotificationMsg(MethodCall call, Result result) {
    try {
      result.success(PreferenceUtils.getData(mRegistrar.activity(), Consts.UMENG_NOTIFICATION_MSG));
    } catch (Exception e) {
      result.success("");
    }
  }

  private void clearCachedNotificationMsg(MethodCall call, Result result) {
    try {
      PreferenceUtils.clearData(mRegistrar.activity(), Consts.UMENG_NOTIFICATION_MSG);
      result.success(true);
    } catch (Exception e) {
      result.success(false);
    }
  }

  public static class MsgInfo {
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
