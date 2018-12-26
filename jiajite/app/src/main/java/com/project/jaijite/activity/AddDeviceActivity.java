package com.project.jaijite.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.mxchip.ftc_service.FTC_Listener;
import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.event.UpdateDeviceDataEvent;
import com.project.jaijite.event.WifiStatusEvent;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.util.EasyLinkTXTRecordUtil;
import com.project.jaijite.util.EasyLinkUtil;
import com.project.jaijite.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AddDeviceActivity extends BaseTitleActivity {
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPwd)
    EditText etPwd;
    String TAG = this.getClass().getName();
    public static InetAddress intf = null;
    public static JmDNS jmdns = null;
    public static WifiManager wm = null;
    public static WifiManager.MulticastLock lock = null;
    public static SampleListener sl = null;
    //扫描任务
    Disposable scanSubscribe;
    int deviceTypeFlag = 0; //0-添加设备 1-已有设备
    String wifiName = "", wifiPwd = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_device;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setTvTitle("添加设备");
        setTitleLeft("设置", R.mipmap.ic_back);
        boolean wifiEnabled = EasyLinkUtil.getEasyLink().isWifiEnabled();
        etName.setEnabled(false);
        if (!wifiEnabled) {
            etName.setText("请开启手机WIFI后重试");
        } else {
            etName.setText(EasyLinkUtil.getSSID());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WifiStatusEvent event) {
        if (event.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            etName.setEnabled(true);
            etName.setText("");
        }
    }

    @OnClick(R.id.btAddDevice)
    void addDevice() {
        wifiName = etName.getText().toString();
        wifiPwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(wifiName)) {
            ToastUtils.showShort("请输入名称");
            return;
        }
        if (TextUtils.isEmpty(wifiPwd)) {
            ToastUtils.showShort("请输入密码");
            return;
        }
        deviceTypeFlag = 0;
        startEasyLink(wifiName, wifiPwd);
        search();
    }

    private void startEasyLink(final String name, final String pwd) {
        EasyLinkUtil.startDeviceDiscovery(pwd, new FTC_Listener() {
            @Override
            public void onFTCfinished(Socket s, String jsonString) {
                ToastUtils.showShortSafe("设备配网成功");
                stopEasyLink();
            }

            @Override
            public void isSmallMTU(int MTU) {
                ToastUtils.showShortSafe("配网失败");
                hideLoading();
            }
        });
    }

    private void stopEasyLink() {
        EasyLinkUtil.stopDeviceDiscovery();
    }

    private void scanDevice() {
        try {
            if (intf == null) {
                intf = getLocalIpAddress();
            }

            if (jmdns == null) {
                jmdns = JmDNS.create(intf);
            }

            if (intf != null && jmdns != null) {
                if (wm == null)
                    wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                lock = wm.createMulticastLock("mylock");
                lock.setReferenceCounted(true);
                lock.acquire();
                sl = new SampleListener();
                jmdns.addServiceListener("_easylink._tcp.local.", sl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopScan() {
        hideLoading();
        if (scanSubscribe != null && !scanSubscribe.isDisposed())
            scanSubscribe.dispose();
        if (jmdns != null && sl != null)
            jmdns.removeServiceListener("_easylink._tcp.local.", sl);
//        if (lock != null) {
//            lock.release();
//        }
//        lock = null;
        jmdns = null;
        sl = null;
    }

    public InetAddress getLocalIpAddress() {
        if (wm == null)
            wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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


    @OnClick(R.id.btSearch)
    void search() {
        deviceTypeFlag = 1;
        showLoading("正在扫描设备", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopScan();
            }
        });
        if (scanSubscribe != null && !scanSubscribe.isDisposed())
            scanSubscribe.dispose();
        scanSubscribe = Observable
                .interval(0, 3000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        scanDevice();//每三秒运行一次 否则时间可能没有数据
                        return aLong;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong >= 20) {
                            ToastUtils.showShortSafe("扫描设备超时");
                            stopScan();
                        }
                    }
                });
    }


    class SampleListener implements ServiceListener, ServiceTypeListener {

        public void serviceAdded(ServiceEvent event) {
            ServiceInfo sInfo = jmdns.getServiceInfo("_easylink._tcp.local.",
                    event.getName());
            if (null != sInfo) {
                String mac = "MAC:".concat(EasyLinkTXTRecordUtil.setDeviceMac(String.valueOf(sInfo.getTextString())));
                DeviceInfo existsDevice = DeviceDB.isExistsDevice(mac);
                List<DeviceInfo> allDeviceData = DeviceDB.getAllDeviceData();
                //添加设备
                String showName = "JR_LINK_" + (allDeviceData.size() + 1);
                DeviceInfo info = new DeviceInfo(null,
                        showName,
                        wifiName,
                        wifiPwd,
                        "IP:".concat(EasyLinkTXTRecordUtil.setDeviceIP(String.valueOf(sInfo.getAddress()))),
                        mac,
                        sInfo.getName(),
                        sInfo.getType(),
                        sInfo.getPort(),
                        false);
                if (deviceTypeFlag == 0) {//在添加设备情况下 如果搜索到新设备则停止扫描并添加设备 反之则继续扫描直到超时
                    ToastUtils.showShortSafe("搜索到新设备");
                } else {//在扫描设备情况下 如果搜索到已有设备则停止扫描 否则继续扫描直到超时
                    if (existsDevice != null) {
                        info.setShowName(existsDevice.getShowName());
                        info.setCheck(existsDevice.getCheck());
                    }
                    ToastUtils.showShortSafe("已发现设备");
                }
                DeviceDB.updateOrInsert(info);
                EventBus.getDefault().post(new UpdateDeviceDataEvent());
                stopScan();
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

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopEasyLink();
        stopScan();
    }
}
