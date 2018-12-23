package com.project.jaijite.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.ProgressBar;

import com.mxchip.ftc_service.FTC_Listener;
import com.mxchip.ftc_service.FTC_Service;
import com.project.jaijite.KittApplication;

import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.api.EasylinkP2P;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;

public class EasyLinkUtil {
    static EasyLink el;
    static EasylinkP2P elp2p;

    public static EasyLink getEasyLink() {
        if (el == null) {
            el = new EasyLink(KittApplication.getApplication());
        }
        return el;
    }

    public static EasylinkP2P getEasyLinkP2P() {
        if (elp2p == null) {
            elp2p = new EasylinkP2P(KittApplication.getApplication());
        }
        return elp2p;
    }

    /**
     * 参数名	类型	默认值	描述
     * ssid	String	无默认值，不可为空	当前wifi的名称
     * password	String	无默认值，可为空	当前wifi的密码(8-64个字节，越长配网速度越慢)
     * isSendIP	Boolean	默认值为false，可为空	是否发送手机的IP，默认不发送，如果此参数为false，那么extraData也不可用
     * runSecond	int	默认值60000，可为空	发送持续的时间，到点了就停止发送, 单位ms
     * sleeptime	int	默认值50，可为空	每包数据的间隔时间，建议20-200, 单位ms
     * extraData	String	无默认值，可为空	需要发送给设备的额外信息
     * rc4key	String	无默认值，可为空	如果需要RC4加密，这里就输入字符串密钥
     *
     * @return
     */

    public static EasyLinkParams getParam(String pwd) {
        return getParam(getSSID(), pwd);
    }

    public static EasyLinkParams getParam(String ssid, String pwd) {
        EasyLinkParams params = new EasyLinkParams();
        params.ssid = ssid;
        params.password = "";
        params.runSecond = 20000;
        params.sleeptime = 50;
        return params;
    }

    public static String getSSID() {
        return getEasyLink().getSSID();
    }

    static WifiManager.MulticastLock multicastLock;

    public static void startDeviceDiscovery(String pwd, FTC_Listener ftc_Listener) {
        WifiManager mWifiManager = (WifiManager) KittApplication
                .getApplication()
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (multicastLock == null)
            multicastLock = mWifiManager.createMulticastLock("multicast.test");
        multicastLock.acquire();
        WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
//        int gatwayVal=	mWifiManager.getDhcpInfo().gateway;
//        String gateWayIP = String.format("%d.%d.%d.%d", (gatwayVal & 0xff), (gatwayVal >> 8 & 0xff), (gatwayVal >> 16 & 0xff), (gatwayVal >> 24 & 0xff));
        String ssid = EasyLinkWifiManager
                .removeSSIDQuotes(connectionInfo.getSSID());
        int ipAddress = connectionInfo.getIpAddress();
        FTC_Service ftcService = FTC_Service.getInstence();
        ftcService.transmitSettings(ssid, pwd, ipAddress,
                ftc_Listener);
    }

    public static void stopDeviceDiscovery() {
        if (multicastLock != null)
            multicastLock.release();
        multicastLock = null;
        FTC_Service.getInstence().stopTransmitting();
    }
}
