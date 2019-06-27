package com.startupcloud.umeng.flutter_umeng;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * @author luopeng Created at 2019/6/26 11:24
 */
public class FlutterUmengNotificationClickHandler extends UmengNotificationClickHandler {
  private UmengMsgArrivedCallback mCallback;

  public FlutterUmengNotificationClickHandler(UmengMsgArrivedCallback callback) {
    mCallback = callback;
  }

  @Override
  public void dealWithCustomAction(Context context, final UMessage uMessage) {
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
          if (mCallback != null) {
            mCallback.msgArrived(Consts.EventTypeConsts.MSG_EVENT, Consts.SourceTypeConsts.NOTIFICATION_CLICK_MSG, uMessage.custom);
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}