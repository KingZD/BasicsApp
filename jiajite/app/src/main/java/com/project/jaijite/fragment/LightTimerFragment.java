package com.project.jaijite.fragment;

import com.project.jaijite.R;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.gui.TimerPickerView;
import com.project.jaijite.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LightTimerFragment extends BaseFragment {
    @BindView(R.id.tpTimer)
    TimerPickerView tpTimer;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_light_timer;
    }

    @Override
    public void initView() {
        tpTimer.setListener(new TimerPickerView.OnTimerListener() {
            @Override
            public void getTime(int ms) {
                ToastUtils.showShort(String.valueOf(ms));
            }

            @Override
            public void lightStatus(boolean isOpen) {
                ToastUtils.showShort(String.valueOf(isOpen));
            }
        });
    }

    @OnClick(R.id.ivColor)
    void showColorFragment() {
        ((LightSettingActivity) getActivity()).showLightColorFragment();
    }
}
