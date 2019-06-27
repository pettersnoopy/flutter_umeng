package com.startupcloud.umeng.flutter_umeng;

/**
 * @author luopeng
 * Created at 2019/6/26 11:28
 */
public interface UmengMsgArrivedCallback {
    /**
     * 消息到达回调
     * @param eventType _event channel
     * @param sourceType 来源，消息类，通知点击类
     * @param msgJson 数据
     */
    void msgArrived(int eventType, int sourceType, String msgJson);
}
