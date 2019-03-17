package com.project.jaijite.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.project.jaijite.R;
import com.project.jaijite.adapter.AddLedAdapter;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.Light;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AddLedActivity extends BaseTitleActivity {
    @BindView(R.id.rlList)
    RecyclerView rlList;
    private String[] urls = {"https://item.taobao.com/item.htm?id=520867873521", "https://item.taobao.com/item.htm?id=520872340403", "https://item.taobao.com/item.htm?id=21306396973", "https://item.taobao.com/item.htm?id=520874848093", "https://item.taobao.com/item.htm?id=16601462518"};
    private AddLedAdapter adapter;
    private List<Light> listData;
    private final int REQUEST_CODE_SCAN = 10000;
    private LightInfo lightInfo;
    public static String PARAM = "param";

    public void initData() {
        this.listData = new ArrayList<>();
        this.listData.add(new Light(R.mipmap.icon_light_shuijing, "水晶灯50W", "1560", this.urls[0]));
        this.listData.add(new Light(R.mipmap.icon_addlight_diaodeng, "吊灯21W", "158", this.urls[1]));
        this.listData.add(new Light(R.mipmap.icon_addlight_tongdeng, "筒灯7W", "38", this.urls[2]));
        this.listData.add(new Light(R.mipmap.icon_addlight_xiding, "吸顶灯22W", "188", this.urls[3]));
        this.listData.add(new Light(R.mipmap.icon_addlight_qiupao, "球泡灯5W", "23", this.urls[4]));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_led;
    }

    @Override
    public void initView() {
        initData();
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        adapter = new AddLedAdapter();
        rlList.setAdapter(adapter);
        rlList.setLayoutManager(new LinearLayoutManager(this));
        rlList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.replaceData(listData);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(AddLedActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.PARAM_URL, listData.get(position).getUrl());
                intent.putExtra(WebViewActivity.PARAM_NAME, listData.get(position).getName());
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @OnClick(R.id.ivAddLed)
    void addLed() {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(GroupActivity.PARAM, lightInfo);
        startActivity(intent);
    }

    @OnClick(R.id.ivScan)
    void scan() {
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent(AddLedActivity.this, CaptureActivity.class);
                            /*ZxingConfig是配置类
                             *可以设置是否显示底部布局，闪光灯，相册，
                             * 是否播放提示音  震动
                             * 设置扫描框颜色等
                             * 也可以不传这个参数
                             * */
                            ZxingConfig config = new ZxingConfig();
                            config.setPlayBeep(true);//是否播放扫描声音 默认为true
                            config.setShake(true);//是否震动  默认为true
                            config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                            config.setReactColor(R.color.black);//设置扫描框四个角的颜色 默认为白色
                            config.setFrameLineColor(R.color.white);//设置扫描框边框颜色 默认无色
                            config.setScanLineColor(R.color.green);//设置扫描线的颜色 默认白色
                            config.setFullScreenScan(true);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                            startActivityForResult(intent, REQUEST_CODE_SCAN);
                        } else {
                            ToastUtils.showShort("缺少权限");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText(this, "扫描结果为：" + content, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 生成二维码
     */
    private void createCode() {
        String contentEtString = "生成二维码实例";

        if (TextUtils.isEmpty(contentEtString)) {
            Toast.makeText(this, "contentEtString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        /*
         * contentEtString：字符串内容
         * w：图片的宽
         * h：图片的高
         * logo1：不需要logo的话直接传null
         * */

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);

    }
}
