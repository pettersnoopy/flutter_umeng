package com.startupcloud.umeng.flutter_umeng;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * @author luopeng Created at 2019/6/27 18:30
 */
public class SystemPushActivity extends UmengNotifyClickActivity {
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
  }

  @Override
  public void onMessage(Intent intent) {
    super.onMessage(intent);
    String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
    try {
      if (TextUtils.isEmpty(body)) {
        post(new Runnable() {
          @Override
          public void run() {
            finish();
          }
        });
        return;
      }
      JSONObject jsonObject = new JSONObject(body).getJSONObject("body");
      final String custom = jsonObject.getString("custom");
      final Context context = SystemPushActivity.this.getApplicationContext();
      PreferenceUtils.saveData(context, Consts.UMENG_NOTIFICATION_MSG, new FlutterUmengPlugin.MsgInfo(Consts.EventTypeConsts.MSG_EVENT, Consts.SourceTypeConsts.NOTIFICATION_CLICK_MSG, custom).toString());
      post(new Runnable() {
        @Override
        public void run() {
          String scheme = "umeng://flutter_umeng";
          try {
            JSONObject object = new JSONObject(custom);
            scheme = object.optString("scheme", "umeng://flutter_umeng");
          } catch (Exception e) {
            e.printStackTrace();
          }
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          context.startActivity(intent);
          finish();
        }
      });
    } catch (Exception e) {
      post(new Runnable() {
        @Override
        public void run() {
          finish();
        }
      });
    }
  }

  private void post(Runnable runnable) {
    new Handler(Looper.getMainLooper()).post(runnable);
  }

  private void saveNotificationData(String data) {

  }
}
