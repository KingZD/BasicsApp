package com.project.jaijite.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseActivity;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.event.UpdateDeviceDataEvent;
import com.project.jaijite.event.WifiStatusEvent;
import com.project.jaijite.fragment.LightingFragment;
import com.project.jaijite.fragment.SettingFragment;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    BaseFragment baseFragment;
    LightingFragment lightingFragment;
    SettingFragment settingFragment;
    NetworkReceiver networkReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        select(R.id.rbLight);
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.ACCESS_WIFI_STATE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            init();
                        } else {
                            ToastUtils.showShort("申请WIFI权限失败");
                        }
                    }
                });
    }

    public void select(View view) {
        select(view.getId());
    }

    public void select(int resId) {
        switch (resId) {
            case R.id.rbLight:
                if (lightingFragment == null) {
                    lightingFragment = new LightingFragment();
                }
                replace(lightingFragment);
                break;
            case R.id.rbSetting:
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                } else {
                    EventBus.getDefault().post(new UpdateDeviceDataEvent());
                }
                replace(settingFragment);
                break;
        }
    }

    private void replace(BaseFragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            if (baseFragment == null) {
                fragmentTransaction.add(R.id.flBody, fragment).show(fragment);
            } else {
                fragmentTransaction.add(R.id.flBody, fragment).hide(baseFragment).show(fragment);
            }
        } else {
            fragmentTransaction.hide(baseFragment).show(fragment);
        }
        baseFragment = fragment;
        fragmentTransaction.commit();
    }

    private void init() {
        //在代码中实现动态注册的方式
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, filter);
    }

    public class NetworkReceiver extends BroadcastReceiver {
        private final String TAG = NetworkReceiver.class.getName();

        private String getConnectionType(int type) {
            String connType = "";
            if (type == ConnectivityManager.TYPE_MOBILE) {
                connType = "3G网络数据";
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                connType = "WIFI网络";
            }
            return connType;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                //拿到wifi的状态值
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_NEW_STATE, 0);
                Log.i(TAG, "wifiState = " + wifiState);
                EventBus.getDefault().post(new WifiStatusEvent(wifiState));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }
}
