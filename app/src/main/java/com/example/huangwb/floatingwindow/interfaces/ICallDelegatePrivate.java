package com.example.huangwb.floatingwindow.interfaces;

/**
 * Created by quanshi on 2015/12/7.
 */
public interface ICallDelegatePrivate {
    /**
     * 通知用户列表刷新
     */
    void onUserListUpdated();

    /**
     * 通知免提状态变化
     */
    void onLoudSpeakerStatusChanged(boolean bLoudSpeaker);

    /**
     * 通知说话人变化
     */
    void onIsSpeakingChanged(String strSpeakingNames);


    /**
     * 桌面共享开始
     */
    void onDesktopShared();

    /**
     * 桌面共享停止
     */
    void onDesktopShareStopped();

    /**
     * 桌面首帧显示通知
     */
    void onDesktopViewerShowed();
    /**
     * 桌面显示关闭通知
     */
    void onDesktopViewerStopped();

    void onVideoCallClosed();

}
