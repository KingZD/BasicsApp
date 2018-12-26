package com.project.jaijite.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.mxchip.ftc_service.FTC_Listener;
import com.mxchip.ftc_service.FTC_Service;
import com.project.jaijite.KittApplication;
import com.project.jaijite.activity.AddDeviceActivity;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.event.UpdateDeviceDataEvent;
import com.project.jaijite.greendao.db.DeviceDB;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.api.EasylinkP2P;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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


    //扫描任务
    static InetAddress intf = null;
    static JmDNS jmdns = null;
    static WifiManager wm = null;
    static WifiManager.MulticastLock lock = null;
    static SampleListener sl = null;
    static Disposable scanSubscribe;

    /**
     * 当前打开的设备地址
     * 不适用数据库存储的ip地址是因为可能ip地址会更换，且用户没有扫描更新设备
     *
     * @param listener 监听
     * @param timer    尝试次数
     */
    public static void getDeviceInfoByDefaultMac(final ScanReadyDeviceListener listener, final int timer) {
        boolean hasDevice = false;
        List<DeviceInfo> allDeviceData = DeviceDB.getAllDeviceData();
        for (DeviceInfo info : allDeviceData) {
            if (info.getCheck()) {
                hasDevice = true;
                getDeviceInfoByMac(info.getMac(), listener, timer);
                break;
            }
        }
        if (!hasDevice && listener != null)
            listener.notHaveOpenDevice();
    }

    /**
     * 当前打开的设备地址
     *
     * @param mac      设备地址
     * @param listener 监听
     * @param timer    尝试次数
     */
    public static void getDeviceInfoByMac(final String mac, final ScanReadyDeviceListener listener, final int timer) {
        if (TextUtils.isEmpty(mac)) {
            if (listener != null)
                listener.notHaveOpenDevice();
            return;
        }
        if (scanSubscribe != null && !scanSubscribe.isDisposed())
            scanSubscribe.dispose();
        scanSubscribe = Observable
                .interval(0, 1000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                            scanDevice(mac, listener);//每三秒运行一次 否则时间可能没有数据
                        return aLong;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong >= timer) {
                            if (listener != null)
                                listener.timeout();
                            stopScan();
                        }
                    }
                });
    }

    public static void stopScan() {
        if (scanSubscribe != null && !scanSubscribe.isDisposed())
            scanSubscribe.dispose();
        if (jmdns != null && sl != null)
            jmdns.removeServiceListener("_easylink._tcp.local.", sl);
        if (lock != null) {
            lock.release();
        }
        lock = null;
        jmdns = null;
        sl = null;
    }

    private static void scanDevice(String mac, ScanReadyDeviceListener listener) {
        try {
            if (intf == null) {
                intf = getLocalIpAddress();
            }

            if (jmdns == null) {
                jmdns = JmDNS.create(intf);
            }

            if (intf != null && jmdns != null) {
                if (wm == null)
                    wm = (WifiManager) KittApplication
                            .getApplication()
                            .getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);
                lock = wm.createMulticastLock("mylock");
                lock.setReferenceCounted(true);
                lock.acquire();
                sl = new SampleListener(mac, listener);
                jmdns.addServiceListener("_easylink._tcp.local.", sl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static InetAddress getLocalIpAddress() {
        if (wm == null)
            wm = (WifiManager) KittApplication
                    .getApplication()
                    .getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wm.getConnectionInfo();
        int intaddr = wifiinfo.getIpAddress();
        byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff),
                (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
                (byte) (intaddr >> 24 & 0xff)};
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            ToastUtils.showShortSafe("获取IP地址失败，请检查网络");
        }
        return addr;
    }


    private static class SampleListener implements ServiceListener, ServiceTypeListener {
        String mac;
        ScanReadyDeviceListener listener;

        SampleListener(String mac, ScanReadyDeviceListener listener) {
            this.mac = mac;
            this.listener = listener;
        }

        public void serviceAdded(ServiceEvent event) {
            ServiceInfo sInfo = jmdns.getServiceInfo("_easylink._tcp.local.",
                    event.getName());
            if (null != sInfo) {
                String mac = "MAC:".concat(EasyLinkTXTRecordUtil.setDeviceMac(String.valueOf(sInfo.getTextString())));
                if (this.mac.equals(mac)) {
                    DeviceInfo info = new DeviceInfo(null,
                            sInfo.getName(),
                            "",
                            "",
                            "IP:".concat(EasyLinkTXTRecordUtil.setDeviceIP(String.valueOf(sInfo.getAddress()))),
                            mac,
                            sInfo.getName(),
                            sInfo.getType(),
                            sInfo.getPort(),
                            false);
                    stopScan();
                    Observable
                            .just(info)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<DeviceInfo>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(DeviceInfo deviceInfo) {
                                    if (listener != null)
                                        listener.getReadyDeviceInfo(deviceInfo);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (listener != null)
                                        listener.error(e.getLocalizedMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    if (listener != null)
                        listener.notHaveOpenDevice();
                }
            }
        }

        public void serviceRemoved(ServiceEvent event) {
            Log.i("REMOVE", "service type = " + event.getType() + ", name = "
                    + event.getName());
        }

        public void serviceResolved(ServiceEvent event) {
            Log.i("RESOLVE", "service type = " + event.getType() + ", name = "
                    + event.getName());
        }

        public void serviceTypeAdded(ServiceEvent event) {
            Log.i("TYPE-ADDED", "service type = " + event.getType()
                    + ", name = " + event.getName());
        }
    }


    public interface ScanReadyDeviceListener {
        void getReadyDeviceInfo(DeviceInfo deviceInfo);

        void timeout();

        void notHaveOpenDevice();

        void error(String msg);
    }
}
