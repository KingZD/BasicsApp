package com.project.jaijite.activity;

import android.view.View;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.bean.Task;
import com.project.jaijite.dialog.GroupIssueDialog;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.ScreenUtils;
import com.project.jaijite.util.ToastUtils;

import butterknife.OnClick;

public class GroupActivity extends BaseTitleActivity {
    public static String PARAM = "param";
    private LightInfo lightInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_group;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
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
    void selectGroup(View v) {
        ScreenUtils.shake();
        Task task = new Task();
        task.setOnlyAttribute(true);
        switch (v.getId()) {
            case R.id.logroup1:
                task.setAttribute("MARKLED:" + lightInfo.getLedId() + "1000");
                break;
            case R.id.logroup2:
                task.setAttribute("MARKLED:" + lightInfo.getLedId() + "0100");
                break;
            case R.id.logroup3:
                task.setAttribute("MARKLED:" + lightInfo.getLedId() + "0010");
                break;
            case R.id.logroup4:
                task.setAttribute("MARKLED:" + lightInfo.getLedId() + "0001");
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
