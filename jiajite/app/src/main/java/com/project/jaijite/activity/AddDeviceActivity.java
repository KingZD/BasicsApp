package com.project.jaijite.activity;

import android.text.TextUtils;
import android.widget.EditText;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.event.UpdateDeviceDataEvent;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

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
        setTvTitle("添加设备");
        setTitleLeft("设置", R.mipmap.ic_back);
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
