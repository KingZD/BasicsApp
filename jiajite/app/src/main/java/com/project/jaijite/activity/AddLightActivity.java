package com.project.jaijite.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.project.jaijite.R;
import com.project.jaijite.adapter.AddLightAdapter;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.event.UpdateLightDataEvent;
import com.project.jaijite.greendao.db.LightingDB;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AddLightActivity extends BaseTitleActivity {
    @BindView(R.id.rlList)
    RecyclerView rlList;
    AddLightAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_light;
    }

    @Override
    public void initView() {
        setTvTitle("添加灯");
        setTitleLeft("取消", -1);
        setTitleRight("确认", -1);
        rlList.setLayoutManager(new LinearLayoutManager(this));
        List<LightInfo> allDelLightData = LightingDB.getAllDelLightData();
        mAdapter = new AddLightAdapter();
        rlList.setAdapter(mAdapter);
        mAdapter.replaceData(allDelLightData);
    }

    @OnClick(R.id.btRight)
    void addLight() {
        List<LightInfo> datas = mAdapter.getData();
        Iterator<LightInfo> iterator = datas.iterator();
        while (iterator.hasNext()) {
            LightInfo info = iterator.next();
            if (!info.getIsCheck())
                iterator.remove();
        }
        LightingDB.updateLight(datas);
        EventBus.getDefault().post(new UpdateLightDataEvent());
        finish();
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }
}
