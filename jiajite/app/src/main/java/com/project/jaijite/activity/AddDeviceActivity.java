package com.project.jaijite.activity;

import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.EditText;

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

import butterknife.BindView;
import butterknife.OnClick;

public class AddDeviceActivity extends BaseTitleActivity {
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etPwd)
    EditText etPwd;

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
        if(!wifiEnabled){
            etName.setEnabled(false);
            etName.setText("请开启手机WIFI后重试");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WifiStatusEvent event) {
        if(event.getWifiState() == WifiManager.WIFI_STATE_ENABLED){
            etName.setEnabled(true);
            etName.setText("");
        }
    }

    @OnClick(R.id.btAddDevice)
    void addDevice() {
        String name = etName.getText().toString();
        String pwd = etPwd.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtils.showShort("请输入名称");
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            ToastUtils.showShort("请输入密码");
            return;
        }
        DeviceInfo info = new DeviceInfo();
        info.setName(name);
        info.setPwd(pwd);
        DeviceDB.insertDeviceData(info);
        EventBus.getDefault().post(new UpdateDeviceDataEvent());
        finish();
    }

    @OnClick(R.id.btSearch)
    void search() {

    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }
}
