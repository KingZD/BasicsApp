package com.project.jaijite.activity;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.dialog.GroupIssueDialog;

import butterknife.OnClick;

public class GroupActivity extends BaseTitleActivity {
    public static String PARAM = "param";

    @Override
    public int getLayoutId() {
        return R.layout.activity_group;
    }

    @Override
    public void initView() {
        String param = getIntent().getStringExtra("PARAM");
        setTvTitle("分组");
        setTitleLeft("添加灯", R.mipmap.ic_back);
        setTitleRight("说明");
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @OnClick(R.id.btRight)
    void showDetail() {
        new GroupIssueDialog(this).show();
    }

    @OnClick({R.id.logroup1, R.id.logroup2, R.id.logroup3, R.id.logroup4})
    void selectGroup() {

    }
}
