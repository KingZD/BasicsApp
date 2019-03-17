package com.project.jaijite.fragment;

import android.view.View;

import com.project.jaijite.R;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.bean.Info;
import com.project.jaijite.bean.Task;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.gui.TimerPickerView;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.ScreenUtils;
import com.project.jaijite.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LightTimerFragment extends BaseFragment {
    public static String PARAM = "param";
    @BindView(R.id.tpTimer)
    TimerPickerView tpTimer;
    private LightInfo lightInfo;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_light_timer;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getArguments().getSerializable(PARAM);
        tpTimer.setCurrentLightStatus(lightInfo.getIsCheck());
        tpTimer.setListener(new TimerPickerView.OnTimerListener() {
            @Override
            public void getTime(final int ms) {
                System.out.println(ms);
                lightInfo.setLight_level(ms);
                Task task = new Task();
                task.setLedID(lightInfo.getLedId());
                task.setGroupID(lightInfo.getGroupId());
                task.setFunction(Task.LIGHT_OPEN);
                task.setAttribute(lightInfo.getLight_level());

                task.startCommand(new MainService.TaskListener() {
                    @Override
                    public void taskCallback(Object... obj) {
//                        ToastUtils.showLongSafe(obj.toString());
                        lightInfo.setIsCheck(true);
                        ((LightSettingActivity) getActivity()).updateSwitch(lightInfo);
                    }

                    @Override
                    public void taskFailed(String msg) {
//                        ToastUtils.showLongSafe(msg);
                    }
                });
            }

            @Override
            public boolean lightStatus(final boolean isOpen, final TimerPickerView.ClickStatus status) {
                ScreenUtils.shake();
                Task task = new Task();
                task.setLedID(lightInfo.getLedId());
                task.setGroupID(lightInfo.getGroupId());
                task.setFunction(Task.LIGHT_OPEN);
                task.setAttribute(!lightInfo.getIsCheck() ? lightInfo.getLight_level() : 0);
                task.startCommand(new MainService.TaskListener() {
                    @Override
                    public void taskCallback(Object... obj) {
                        ToastUtils.showLongSafe(obj.toString());
                        lightInfo.setIsCheck(!isOpen);
                        ((LightSettingActivity) getActivity()).updateSwitch(lightInfo);
                    }

                    @Override
                    public void taskFailed(String msg) {
                    }
                });
                return false;
            }
        });
    }

    @OnClick({R.id.ivOne, R.id.ivTwo, R.id.ivThree, R.id.ivFour, R.id.ivFive, R.id.ivSix})
    void changeValue(View v) {
        ScreenUtils.shake();
        Task task = new Task();
        task.setLedID(lightInfo.getLedId());
        task.setGroupID(lightInfo.getGroupId());
        task.setFunction(Task.LIGHT_COLOR_TEMPERATURE);
        switch (v.getId()) {
            case R.id.ivOne:
                lightInfo.setColor_temp(3000);
                break;
            case R.id.ivTwo:
                lightInfo.setColor_temp(4000);
                break;
            case R.id.ivThree:
                lightInfo.setColor_temp(5000);
                break;
            case R.id.ivFour:
                lightInfo.setColor_temp(6000);
                break;
            case R.id.ivFive:
                lightInfo.setColor_temp(7000);
                break;
            case R.id.ivSix:
                lightInfo.setColor_temp(8000);
                break;
        }
        LightingDB.updateLight(lightInfo);
        task.setAttribute(lightInfo.getColor_temp());
        task.startCommand(new MainService.TaskListener() {
            @Override
            public void taskCallback(Object... obj) {
                ToastUtils.showLongSafe(obj.toString());
            }

            @Override
            public void taskFailed(String msg) {
            }
        });
    }

    @OnClick(R.id.ivColor)
    void showColorFragment() {
        ((LightSettingActivity) getActivity()).showLightColorFragment();
    }

    public void updateSwitch(LightInfo lightInfo) {
        if (tpTimer != null)
            tpTimer.updateSwitch(lightInfo.getIsCheck());
    }

    public void sysnLightInfo(LightInfo lightInfo) {
        this.lightInfo = lightInfo;
    }
}
