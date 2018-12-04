package com.project.jaijite.activity;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.LightInfo;

public class LightSettingActivity extends BaseTitleActivity {
    public static final String PARAM = "param";
    private LightInfo lightInfo;

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
    }
}
