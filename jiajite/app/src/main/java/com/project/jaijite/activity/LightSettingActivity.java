package com.project.jaijite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.bean.Info;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.fragment.LightColorFragment;
import com.project.jaijite.fragment.LightTimerFragment;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.util.ScreenUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;

public class LightSettingActivity extends BaseTitleActivity {
    @BindViews({R.id.ivGroupOne, R.id.ivGroupTwo, R.id.ivGroupThree, R.id.ivGroupFour})
    List<ImageView> groups;
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
        String groupId = lightInfo.getGroupId();
        for (int i = 0; i < groupId.length(); i++) {
            Picasso.with(this)
                    .load(ScreenUtils.getResId("group_" +
                            (TextUtils.equals(groupId.substring(i, i + 1), String.valueOf(Info.TURN_ON)) ? "open_" : "close_") +
                            (i + 1)))
                    .into(groups.get(i));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lightInfo != null)
            lightInfo = LightingDB.refreshLightInfo(lightInfo);
    }

    @OnClick({R.id.ivGroupOne, R.id.ivGroupTwo, R.id.ivGroupThree, R.id.ivGroupFour})
    void groupAttribute(View v) {
        if (lightInfo == null) return;
        String groupId = lightInfo.getGroupId();
        int index = 0;
        switch (v.getId()) {
            case R.id.ivGroupOne:
                index = 0;
                break;
            case R.id.ivGroupTwo:
                index = 1;
                break;
            case R.id.ivGroupThree:
                index = 2;
                break;
            case R.id.ivGroupFour:
                index = 3;
                break;
        }
        String status = String.valueOf(groupId.charAt(index));
        status = TextUtils.equals(status, String.valueOf(Info.TURN_ON)) ? "0" : "1";
        Picasso.with(this)
                .load(ScreenUtils.getResId("group_" +
                        (TextUtils.equals(status, String.valueOf(Info.TURN_ON)) ? "open_" : "close_") +
                        (index + 1)))
                .into(groups.get(index));
        if (index == 0) {
            groupId = status + groupId.substring(index + 1, groupId.length());
        } else if (index == groupId.length() - 1) {
            groupId = groupId.substring(0, index) + status;
        } else {
            groupId = groupId.substring(0, index) + status + groupId.substring(index + 1, groupId.length());
        }
        lightInfo.setGroupId(groupId);
        LightingDB.updateLight(lightInfo);
        sysnLightInfo(lightInfo);
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @OnClick(R.id.btRight)
    void addLight() {
        Intent intent = new Intent(this, AddLedActivity.class);
        intent.putExtra(PARAM, lightInfo);
        startActivity(intent);
    }

    @OnClick(R.id.ivMusic)
    void showMusic() {
        Intent intent = new Intent(this, MusicModeActivity.class);
        intent.putExtra(PARAM, lightInfo);
        startActivity(intent);
    }

    @OnClick(R.id.ivMicro)
    void showMicro() {
        Intent intent = new Intent(this, MicroModeActivity.class);
        intent.putExtra(PARAM, lightInfo);
        startActivity(intent);
    }

    @OnClick(R.id.ivTimer)
    void showTimer() {
        Intent intent = new Intent(this, TimerModeActivity.class);
        intent.putExtra(PARAM, lightInfo);
        startActivity(intent);
    }

    @OnClick(R.id.ivScene)
    void showScene() {
        Intent intent = new Intent(this, SceneModeActivity.class);
        intent.putExtra(PARAM, lightInfo);
        startActivity(intent);
    }

    public void showLightTimerFragment() {
        if (lightTimerFragment == null) {
            lightTimerFragment = new LightTimerFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(LightTimerFragment.PARAM, lightInfo);
            lightTimerFragment.setArguments(bundle);
        }
        replace(lightTimerFragment);
    }

    public void showLightColorFragment() {
        if (lightColorFragment == null) {
            lightColorFragment = new LightColorFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(LightColorFragment.PARAM, lightInfo);
            lightColorFragment.setArguments(bundle);
        }
        replace(lightColorFragment);
    }

    public void updateSwitch(LightInfo lightInfo) {
        this.lightInfo = lightInfo;
        LightingDB.updateLight(lightInfo);
        if (lightColorFragment != null)
            lightColorFragment.updateSwitch(lightInfo);
        if (lightTimerFragment != null)
            lightTimerFragment.updateSwitch(lightInfo);
    }

    public void sysnLightInfo(LightInfo lightInfo) {
        if (lightColorFragment != null)
            lightColorFragment.sysnLightInfo(lightInfo);
        if (lightTimerFragment != null)
            lightTimerFragment.sysnLightInfo(lightInfo);
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
