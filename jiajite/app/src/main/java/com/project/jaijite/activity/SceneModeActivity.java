package com.project.jaijite.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.project.jaijite.R;
import com.project.jaijite.adapter.SceneAdapter;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.bean.SceneBean;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.gui.RecyclerGridDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SceneModeActivity extends BaseTitleActivity {
    @BindView(R.id.rlList)
    RecyclerView rlList;
    public static final String PARAM = "param";
    private LightInfo lightInfo;
    SceneAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scene_mode;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle("情景模式");
        setTitleLeft(lightInfo.getName());
        rlList.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new SceneAdapter();
        rlList.setAdapter(mAdapter);
        List<SceneBean> data = getData();
        mAdapter.replaceData(data);
        rlList.addItemDecoration(new RecyclerGridDecoration(this, 4, data.size()));
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    private List<SceneBean> getData() {
        List<SceneBean> list = new ArrayList<>();
        list.add(new SceneBean(R.mipmap.ic_lm, "黎明"));
        list.add(new SceneBean(R.mipmap.ic_ml, "明亮"));
        list.add(new SceneBean(R.mipmap.ic_wn, "温暖"));
        list.add(new SceneBean(R.mipmap.ic_read, "阅读"));
        list.add(new SceneBean(R.mipmap.ic_yg, "月光"));
        list.add(new SceneBean(R.mipmap.ic_rgb, "RGB"));
        list.add(new SceneBean(R.mipmap.ic_qs, "千色"));
        list.add(new SceneBean(R.mipmap.ic_water, "水纹"));
        list.add(new SceneBean(R.mipmap.ic_bsb, "爆闪白"));
        list.add(new SceneBean(R.mipmap.ic_bshong, "爆闪红"));
        list.add(new SceneBean(R.mipmap.ic_bslv, "爆闪绿"));
        list.add(new SceneBean(R.mipmap.ic_bsl, "爆闪蓝"));
        list.add(new SceneBean(R.mipmap.ic_bsh, "爆闪黄"));
        list.add(new SceneBean(R.mipmap.ic_frgb, "F-RGB"));
        list.add(new SceneBean(R.mipmap.ic_sos, "SOS"));
        list.add(new SceneBean(R.mipmap.ic_jsd, "警示灯"));
        return list;
    }
}
