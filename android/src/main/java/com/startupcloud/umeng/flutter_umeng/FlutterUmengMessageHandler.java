package com.startupcloud.umeng.flutter_umeng;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

/**
 * @author luopeng
 * Created at 2019/6/26 11:08
 */
public class FlutterUmengMessageHandler extends UmengMessageHandler {
    private UmengMsgArrivedCallback mCallback;
    public FlutterUmengMessageHandler(UmengMsgArrivedCallback callback) {
        mCallback = callback;
    }

    @Override
    public void dealWithNotificationMessage(Context context, UMessage uMessage) {
        super.dealWithNotificationMessage(context, uMessage);
        dealWithUMessage(uMessage);
    }

    @Override
    public void dealWithCustomMessage(Context context, final UMessage uMessage) {
        dealWithUMessage(uMessage);
    }

    private void dealWithUMessage(final UMessage uMessage) {
        try {
            if (uMessage == null) {
                return;
            }
            String msgJson = uMessage.custom;
            if (TextUtils.isEmpty(msgJson)) {
                return;
            }
            dealWithUmengMsg(msgJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealWithUmengMsg(final String msgJson) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.msgArrived(Consts.EventTypeConsts.MSG_EVENT, Consts.SourceTypeConsts.CUSTOM_MSG, msgJson);
                }
            }
        });
    }
}
