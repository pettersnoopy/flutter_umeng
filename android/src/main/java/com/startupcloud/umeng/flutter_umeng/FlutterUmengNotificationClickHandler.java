package com.startupcloud.umeng.flutter_umeng;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import org.json.JSONObject;

/**
 * @author luopeng Created at 2019/6/26 11:24
 */
public class FlutterUmengNotificationClickHandler extends UmengNotificationClickHandler {
  private UmengMsgArrivedCallback mCallback;

  public FlutterUmengNotificationClickHandler(UmengMsgArrivedCallback callback) {
    mCallback = callback;
  }

  @Override
  public void dealWithCustomAction(final Context context, final UMessage uMessage) {
    try {
      if (uMessage == null) {
        return;
      }

      if (TextUtils.isEmpty(uMessage.custom)) {
        return;
      }
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() {
          try {
            String scheme = "umeng://flutter_umeng";
            try {
              JSONObject object = new JSONObject(uMessage.custom);
              scheme = object.optString("scheme", "umeng://flutter_umeng");
            } catch (Exception e) {
              e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            if (mCallback != null) {
              mCallback.msgArrived(Consts.EventTypeConsts.MSG_EVENT, Consts.SourceTypeConsts.NOTIFICATION_CLICK_MSG,
                  uMessage.custom);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
