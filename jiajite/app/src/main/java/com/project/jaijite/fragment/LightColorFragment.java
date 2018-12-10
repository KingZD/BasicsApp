package com.project.jaijite.fragment;

import android.widget.ImageView;

import com.project.jaijite.R;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.gui.ColorPickerView1;
import com.project.jaijite.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LightColorFragment extends BaseFragment {
    @BindView(R.id.cpv)
    ColorPickerView1 tpTimer;
    @BindView(R.id.ivTimer)
    ImageView ivTimer;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_light_color;
    }

    @Override
    public void initView() {
        tpTimer.setListener(new ColorPickerView1.OnTimerListener() {

            @Override
            public void colorChanged(String color) {
                ToastUtils.showShort(color);
            }

            @Override
            public void lightStatus(boolean isOpen) {
                ToastUtils.showShort(String.valueOf(isOpen));
            }
        });
    }

    @OnClick(R.id.ivTimer)
    void showTimerFragment(){
        ((LightSettingActivity)getActivity()).showLightTimerFragment();
    }
}
