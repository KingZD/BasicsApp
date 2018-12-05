package com.project.jaijite.fragment;

import com.project.jaijite.R;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.base.BaseFragment;

import butterknife.OnClick;

public class LightColorFragment extends BaseFragment {
    @Override
    public int getLayoutId() {
        return R.layout.fragment_light_color;
    }

    @Override
    public void initView() {

    }

    @OnClick(R.id.ivTimer)
    void showTimerFragment(){
        ((LightSettingActivity)getActivity()).showLightTimerFragment();
    }
}
