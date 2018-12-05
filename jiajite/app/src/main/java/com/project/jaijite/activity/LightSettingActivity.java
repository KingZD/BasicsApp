package com.project.jaijite.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.fragment.LightColorFragment;
import com.project.jaijite.fragment.LightTimerFragment;

public class LightSettingActivity extends BaseTitleActivity {
    public static final String PARAM = "param";
    private LightInfo lightInfo;
    BaseFragment baseFragment;
    LightColorFragment lightColorFragment;
    LightTimerFragment lightTimerFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_light_setting;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle(lightInfo.getName());
        setTitleLeft("照明");
        setTitleRight("添加灯");
        showLightTimerFragment();
    }


    public void showLightTimerFragment(){
        if (lightTimerFragment == null) {
            lightTimerFragment = new LightTimerFragment();
        }
        replace(lightTimerFragment);
    }

    public void showLightColorFragment(){
        if (lightColorFragment == null) {
            lightColorFragment = new LightColorFragment();
        }
        replace(lightColorFragment);
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
