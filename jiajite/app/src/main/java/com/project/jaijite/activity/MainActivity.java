package com.project.jaijite.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseActivity;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.fragment.LightingFragment;
import com.project.jaijite.fragment.SettingFragment;

public class MainActivity extends BaseActivity {
    BaseFragment baseFragment;
    LightingFragment lightingFragment;
    SettingFragment settingFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        select(R.id.rbLight);
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
                }
                replace(settingFragment);
                break;
        }
    }

    private void replace(BaseFragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            if(baseFragment  == null){
                fragmentTransaction.add(R.id.flBody, fragment).show(fragment);
            }else {
                fragmentTransaction.add(R.id.flBody, fragment).hide(baseFragment).show(fragment);
            }
        } else {
            fragmentTransaction.hide(baseFragment).show(fragment);
        }
        baseFragment = fragment;
        fragmentTransaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }
}
