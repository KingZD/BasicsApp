package com.project.jaijite.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.project.jaijite.R;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.bean.Task;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.gui.ColorPickerView1;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.ScreenUtils;
import com.project.jaijite.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LightColorFragment extends BaseFragment {
    public static String PARAM = "param";
    @BindView(R.id.cpv)
    ColorPickerView1 tpTimer;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    private LightInfo lightInfo;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_light_color;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getArguments().getSerializable(PARAM);
        tpTimer.setCurrentLightStatus(lightInfo.getIsCheck());
        tpTimer.setListener(new ColorPickerView1.OnTimerListener() {

            @Override
            public void colorChanged(int color) {
                String rgb = int2Rgb(color);
                System.out.println(rgb);
                Task task = new Task();
                task.setLedID(lightInfo.getLedId());
                task.setGroupID(lightInfo.getGroupId());
                task.setFunction(Task.LIGHT_RGB);
                task.setAttribute(rgb);
                task.startCommand(new MainService.TaskListener() {
                    @Override
                    public void taskCallback(Object... obj) {
//                        ToastUtils.showLongSafe(obj.toString());
                        lightInfo.setIsCheck(true);
                        ((LightSettingActivity) getActivity()).updateSwitch(lightInfo);
                    }

                    @Override
                    public void taskFailed(String msg) {
                    }
                });
            }

            @Override
            public boolean lightStatus(final boolean isOpen, final ColorPickerView1.ClickStatus status) {
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
                        LightingDB.updateLight(lightInfo);
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

    @OnClick(R.id.ivTimer)
    void showTimerFragment() {
        ((LightSettingActivity) getActivity()).showLightTimerFragment();
    }

    public void updateSwitch(LightInfo lightInfo) {
        if (tpTimer != null)
            tpTimer.updateSwitch(lightInfo.getIsCheck());
    }

    public void sysnLightInfo(LightInfo lightInfo) {
        this.lightInfo = lightInfo;
    }

    /**
     * Color的Int整型转Color的rgb数组
     * colorInt - -12590395
     * return Color的rgb数组 —— [63,226,197]
     */
    public String int2Rgb(int colorInt) {
        int red = (int) (Color.red(colorInt) / 255F * 100);
        int green = (int) (Color.green(colorInt) / 255F * 100);
        int blue = (int) (Color.blue(colorInt) / 255F * 100);
        String r;
        String g;
        String b;
        if (red < 10)
            r = "00" + red;
        else if (red < 100)
            r = "0" + red;
        else
            r = "" + red;

        if (green < 10)
            g = "00" + green;
        else if (green < 100)
            g = "0" + green;
        else
            g = "" + green;

        if (blue < 10)
            b = "00" + blue;
        else if (blue < 100)
            b = "0" + blue;
        else
            b = "" + blue;
        return r + g + b;
    }

    @OnClick({R.id.ivOne, R.id.ivTwo, R.id.ivThree, R.id.ivFour, R.id.ivFive, R.id.ivSix})
    void changeValue(View v) {
        ScreenUtils.shake();
        Task task = new Task();
        task.setLedID(lightInfo.getLedId());
        task.setGroupID(lightInfo.getGroupId());
        task.setFunction(Task.LIGHT_RGB);
        switch (v.getId()) {
            case R.id.ivOne:
                task.setAttribute("100000000");
                break;
            case R.id.ivTwo:
                task.setAttribute("000100000");
                break;
            case R.id.ivThree:
                task.setAttribute("000000100");
                break;
            case R.id.ivFour:
                task.setAttribute("100100000");
                break;
            case R.id.ivFive:
                task.setAttribute("100000100");
                break;
            case R.id.ivSix:
                task.setAttribute("000100100");
                break;
        }
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
}
