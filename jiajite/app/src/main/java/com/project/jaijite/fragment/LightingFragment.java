package com.project.jaijite.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.project.jaijite.R;
import com.project.jaijite.activity.AddLedActivity;
import com.project.jaijite.activity.AddLightActivity;
import com.project.jaijite.activity.LightSettingActivity;
import com.project.jaijite.adapter.LightingAdapter;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.bean.Info;
import com.project.jaijite.bean.Task;
import com.project.jaijite.dialog.TipsDialog;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.event.UpdateLightDataEvent;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.EasyLinkUtil;
import com.project.jaijite.util.SPUtils;
import com.project.jaijite.util.ToastUtils;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.Socket;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LightingFragment extends BaseFragment
        implements BaseQuickAdapter.OnItemChildClickListener {

    @BindView(R.id.rlList)
    RecyclerView rlList;
    LightingAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_light;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setTitleLeft("", -1);
        setTitleRight("", R.mipmap.add);
        setTvTitle("照明");
        rlList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LightingAdapter();
        mAdapter.setOnItemChildClickListener(this);
        rlList.setAdapter(mAdapter);
        rlList.addItemDecoration(new DefaultItemDecoration(ContextCompat
                .getColor(getActivity(), R.color.darkgray)));
        showLoading();
        showLocalData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateLightDataEvent event) {
        showLocalData();
    }

    @OnClick(R.id.btRight)
    void btRight() {
        startActivity(new Intent(getActivity(), AddLightActivity.class));
    }

    //加载数据
    private void showLocalData() {
        showLoading();
        Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                        try {
                            initLightData();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                        emitter.onNext(true);
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean o) {
                        hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                    }

                    @Override
                    public void onComplete() {
                        mAdapter.replaceData(LightingDB.getAllNormalLightData());
                        hideLoading();
                    }
                });
    }

    //初始化灯的数据
    private void initLightData() {
        boolean add = SPUtils.getInstance().getBoolean("add", false);
        if (!add) {
            LightingDB.clear();
            LightingDB.insertLightData("大厅灯");
            LightingDB.insertLightData("小厅灯");
            LightingDB.insertLightData("餐厅灯");
            LightingDB.insertLightData("厨房灯");
            LightingDB.insertLightData("走廊灯");
            LightingDB.insertLightData("楼梯灯");
            LightingDB.insertLightData("阳台灯");
            LightingDB.insertLightData("情景灯");
            LightingDB.insertLightData("主卧灯");
            LightingDB.insertLightData("卧室灯");
            LightingDB.insertLightData("童房灯");
            LightingDB.insertLightData("客房灯");
            LightingDB.insertLightData("书房灯");
            LightingDB.insertLightData("台灯");
            LightingDB.insertLightData("工作灯");
            LightingDB.insertLightData("浴室灯");
            LightingDB.insertLightData("路灯");
            LightingDB.insertLightData("大门灯");
            LightingDB.insertLightData("车库灯");
            LightingDB.insertLightData("厕所灯");
            LightingDB.insertLightData("落地灯");
            LightingDB.insertLightData("天花灯");
            LightingDB.insertLightData("水晶灯");
            LightingDB.insertLightData("蜡烛灯");
            SPUtils.getInstance().put("add", true);
        }
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        try {
            final LightInfo lightInfo = mAdapter.getData().get(position);
            switch (view.getId()) {
                case R.id.ledPowerBtn:
                    showLoading("设备连接中", new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ToastUtils.showShortSafe("连接已取消");
                            EasyLinkUtil.stopScan();
                        }
                    });
                    EasyLinkUtil.getDeviceInfoByDefaultMac(new EasyLinkUtil.ScanReadyDeviceListener() {
                        @Override
                        public void getReadyDeviceInfo(DeviceInfo deviceInfo) {
                            hideLoading();
                            //设置地址
                            Info.SERVER_ADDR = deviceInfo.getIp();
                            Info.PORT = deviceInfo.getPort();
                            Task task = new Task();
                            task.setMac(deviceInfo.getMac());
                            task.setId(lightInfo.getId());
                            task.setLedID(String.valueOf(lightInfo.getId()));
                            task.setFunction(lightInfo.getIsCheck() ? Info.TURN_ON : Info.TURN_OFF);
                            task.setAttribute(String.valueOf(lightInfo.getIsCheck() ? Info.ON : Info.OFF));
                            MainService.newTask(task, true);

                            lightInfo.setLed_state(lightInfo.getIsCheck() ? Info.TURN_ON : Info.TURN_OFF);
                            lightInfo.setIsCheck(!lightInfo.getIsCheck());
                            LightingDB.updateLight(lightInfo);
                            mAdapter.notifyItemChanged(position);
                            ToastUtils.showShortSafe("发送指令成功：".concat(JSON.toJSONString(deviceInfo)));
                        }

                        @Override
                        public void timeout() {
                            hideLoading();
                            EasyLinkUtil.stopScan();
                            ToastUtils.showShortSafe("连接超时");
                        }

                        @Override
                        public void notHaveOpenDevice() {
                            hideLoading();
                            EasyLinkUtil.stopScan();
                            ToastUtils.showShortSafe("请至少开启一个在线设备");
                        }

                        @Override
                        public void error(String msg) {
                            hideLoading();
                            EasyLinkUtil.stopScan();
                            ToastUtils.showShortSafe(msg);
                        }
                    }, 5);//每1s获取一次 10次就是10s
                    break;
                case R.id.btAddLed:
                    startActivity(new Intent(getActivity(), AddLedActivity.class));
                    break;
                case R.id.llBody:
                    Intent intent = new Intent(getActivity(), LightSettingActivity.class);
                    intent.putExtra(LightSettingActivity.PARAM, lightInfo);
                    startActivity(intent);
                    break;
                case R.id.btReName:
                    TipsDialog.getInstance()
                            .createDialog(getActivity(), R.layout.dialog_light_rename)
                            .bindClick(R.id.leftButton, null)
                            .bindClick(R.id.rightButton, new TipsDialog.TipClickListener() {
                                @Override
                                public void onClick(View v, TipsDialog dialog) {
                                    EditText et = dialog.getView(R.id.etContent);
                                    if (TextUtils.isEmpty(et.getText().toString())) {
                                        ToastUtils.showShort("名称不能为空！");
                                    }
                                    lightInfo.setName(et.getText().toString());
                                    LightingDB.updateLight(lightInfo);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setText(R.id.etContent, lightInfo.getName())
                            .show();
                    break;
                case R.id.deleteBtn:
                    TipsDialog.getInstance()
                            .createDialog(getActivity(), R.layout.dialog_light_delete)
                            .bindClick(R.id.leftButton, null)
                            .bindClick(R.id.rightButton, new TipsDialog.TipClickListener() {
                                @Override
                                public void onClick(View v, TipsDialog dialog) {
                                    mAdapter.getData().remove(position);
                                    LightingDB.delLight(lightInfo);
                                    mAdapter.notifyItemRemoved(position);
                                }
                            })
                            .show();
                    break;
            }
        } catch (Exception e) {
            ToastUtils.showShort(e.getMessage());
        }
    }
}
