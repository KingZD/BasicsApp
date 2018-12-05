package com.project.jaijite.fragment;

import com.project.jaijite.R;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.base.BaseFragment;

import butterknife.OnClick;

public class LightTimerFragment extends BaseFragment {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_light_timer;
    }

    @Override
    public void initView() {

    }

    @OnClick(R.id.ivColor)
    void showColorFragment(){
        ((LightSettingActivity)getActivity()).showLightColorFragment();
    }
}
