package com.project.jaijite.activity;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.LightInfo;

import butterknife.OnClick;

public class TimerModeActivity extends BaseTitleActivity {
    public static final String PARAM = "param";
    private LightInfo lightInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_timer_mode;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle("定时模式");
        setTitleLeft(lightInfo.getName());
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }
}
