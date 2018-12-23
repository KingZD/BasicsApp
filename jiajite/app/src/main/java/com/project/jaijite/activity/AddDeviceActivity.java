package com.project.jaijite.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.mxchip.ftc_service.FTC_Listener;
import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.event.UpdateDeviceDataEvent;
import com.project.jaijite.event.WifiStatusEvent;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.util.EasyLinkUtil;
import com.project.jaijite.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
        String name = etName.getText().toString();
        String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("请输入名称");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("请输入密码");
            return;
        }
//        showLoading("配网中", new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                stopEasyLink();
//            }
//        });
        startEasyLink(name, pwd);
        search();
    }

    private void startEasyLink(final String name, final String pwd) {
        EasyLinkUtil.startDeviceDiscovery(pwd, new FTC_Listener() {
            @Override
            public void onFTCfinished(Socket s, String jsonString) {
                DeviceInfo info = new DeviceInfo();
                info.setName(name);
                info.setPwd(pwd);
                DeviceDB.updateOrInsert(info);
                EventBus.getDefault().post(new UpdateDeviceDataEvent());
                ToastUtils.showShortSafe("设备配网成功");
//                hideLoading();
                stopEasyLink();
            }

            @Override
            public void isSmallMTU(int MTU) {
                ToastUtils.showShortSafe("配网失败");
                hideLoading();
            }
        });
//        EasyLinkUtil.getEasyLink().startEasyLink(EasyLinkUtil.getParam(pwd), new EasyLinkCallBack() {
//            @Override
//            public void onSuccess(int code, String message) {
//                DeviceInfo info = new DeviceInfo();
//                info.setName(name);
//                info.setPwd(pwd);
//                DeviceDB.insertDeviceData(info);
//                EventBus.getDefault().post(new UpdateDeviceDataEvent());
//                ToastUtils.showShortSafe("设备配网成功");
//                hideLoading();
//                stopEasyLink();
//                search();
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//
//            }
//        });
    }

    private void stopEasyLink() {
        EasyLinkUtil.stopDeviceDiscovery();
//        EasyLinkUtil.getEasyLink().stopEasyLink(new EasyLinkCallBack() {
//            @Override
//            public void onSuccess(int code, String message) {
//                LogUtils.i(message);
//            }
//
//            @Override
//            public void onFailure(int code, String message) {
//                LogUtils.i(message);
//            }
//        });
    }


    public static InetAddress intf = null;
    public static JmDNS jmdns = null;
    public static WifiManager wm = null;
    public static WifiManager.MulticastLock lock = null;
    public static SampleListener sl = null;

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

    class SampleListener implements ServiceListener, ServiceTypeListener {

        public void serviceAdded(ServiceEvent event) {
            // Log.i("ADD", "service type = " + event.getType() + ", name = "
            // + event.getName() + ",IP:" + jmdns.getHostName()+",port:");
            ServiceInfo sInfo = jmdns.getServiceInfo("_easylink._tcp.local.",
                    event.getName());
            if (null != sInfo) {
                stopScan();
                //添加设备

                Log.i("====", "serviceInfo:" + sInfo.getTextString());
                Log.i("====",
                        "Name:" + sInfo.getName() + "Service:"
                                + sInfo.getType() + "IP:" + sInfo.getAddress()
                                + "port:" + sInfo.getPort()
                                + "Mac:" + sInfo.getPriority());
                ToastUtils.showShortSafe("发现设备 <<<Name:" + sInfo.getName() + "Service:"
                        + sInfo.getType() + "IP:" + sInfo.getAddress()
                        + "port:" + sInfo.getPort()
                        + "Mac:" + sInfo.getPriority() + ">>>");
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

    Disposable scanSubscribe;

    @OnClick(R.id.btSearch)
    void search() {
        showLoading("正在扫描设备", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopScan();
            }
        });
        if (scanSubscribe != null && !scanSubscribe.isDisposed())
            scanSubscribe.dispose();
        scanSubscribe = Observable
                .interval(1000, 3000, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        if (aLong == 0)//只运行一次
                            scanDevice();
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
