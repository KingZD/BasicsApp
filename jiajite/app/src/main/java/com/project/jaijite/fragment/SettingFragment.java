package com.project.jaijite.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.project.jaijite.R;
import com.project.jaijite.activity.AddDeviceActivity;
import com.project.jaijite.activity.ManualActivity;
import com.project.jaijite.adapter.DeviceAdapter;
import com.project.jaijite.base.BaseFragment;
import com.project.jaijite.dialog.TipsDialog;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.event.UpdateDeviceDataEvent;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.gui.SettingHeaderView;
import com.project.jaijite.gui.SwitchButton;
import com.project.jaijite.util.SPUtils;
import com.project.jaijite.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingFragment extends BaseFragment
        implements SettingHeaderView.OnHeaderListener,
        BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.rlList)
    RecyclerView rlList;
    DeviceAdapter mAdapter;
    SettingHeaderView headerView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setTitleLeft("", -1);
        setTvTitle("设置");
        setTitleRight("退出", -1);
        mAdapter = new DeviceAdapter();
        rlList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlList.setAdapter(mAdapter);
        headerView = new SettingHeaderView(getActivity());
        headerView.setOnHeaderListener(this);
        mAdapter.addHeaderView(headerView);
        mAdapter.setOnItemChildClickListener(this);
        showDeviceData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateDeviceDataEvent event) {
        showDeviceData();
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        try {
            final DeviceInfo deviceInfo = mAdapter.getData().get(position);
            switch (view.getId()) {
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
                                    deviceInfo.setShowName(et.getText().toString());
                                    DeviceDB.updateOrInsert(deviceInfo);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setText(R.id.etContent, deviceInfo.getShowName())
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
                                    DeviceDB.delLight(deviceInfo);
                                    mAdapter.notifyItemRemoved(position + 1);
                                }
                            })
                            .setText(R.id.contentTv, "确定删除设备？")
                            .show();
                    break;
            }
        } catch (Exception e) {
            ToastUtils.showShort(e.getMessage());
        }
    }

    /**
     * 更新设备列表
     */
    private void showDeviceData() {
        if (mAdapter != null)
            mAdapter.replaceData(DeviceDB.getAllDeviceData());
    }

    @Override
    public void functionIntroduction() {
        startActivity(new Intent(getActivity(), ManualActivity.class));
    }

    @Override
    public void touch(SwitchButton view, boolean isChecked) {
        SPUtils.getInstance().put("touchShake", isChecked);
    }

    @Override
    public void addDevice() {
        startActivity(new Intent(getActivity(), AddDeviceActivity.class));
    }


    @OnClick(R.id.btRight)
    void logOut() {
        logout();
    }

    @Override
    public void logout() {
        showLoading("正在退出");
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                hideLoading();
                ToastUtils.showShort("登出成功");
                getActivity().finish();
            }

            @Override
            public void onLogoutFailed(int code, String error) {
                hideLoading();
                ToastUtils.showShort("登出失败" + error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (headerView != null)
            headerView.onDstory();
    }
}
