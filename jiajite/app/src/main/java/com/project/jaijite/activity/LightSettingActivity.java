package com.project.jaijite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.fragment.LightColorFragment;
import com.project.jaijite.fragment.LightTimerFragment;

import butterknife.OnClick;

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

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @OnClick(R.id.ivMusic)
    void showMusic() {
        Intent intent = new Intent(this, MusicModeActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    @OnClick(R.id.ivMicro)
    void showMicro() {
        Intent intent = new Intent(this, MicroModeActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    @OnClick(R.id.ivTimer)
    void showTimer() {
        Intent intent = new Intent(this, TimerModeActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    @OnClick(R.id.ivScene)
    void showScene() {
        Intent intent = new Intent(this, SceneModeActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    public void showLightTimerFragment() {
        if (lightTimerFragment == null) {
            lightTimerFragment = new LightTimerFragment();
        }
        replace(lightTimerFragment);
    }

    public void showLightColorFragment() {
        if (lightColorFragment == null) {
            lightColorFragment = new LightColorFragment();
        }
        replace(lightColorFragment);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }
}
