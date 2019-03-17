package com.project.jaijite.adapter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.project.jaijite.R;
import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.greendao.db.DeviceDB;
import com.project.jaijite.gui.SwitchButton;
import com.project.jaijite.util.ToastUtils;

import java.util.List;

public class DeviceAdapter extends BaseQuickAdapter<DeviceInfo, BaseViewHolder> {

    private boolean allowSwipe = true;

    public DeviceAdapter() {
        super(R.layout.item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, final DeviceInfo deviceInfo) {
        SwipeLayout sp = helper.getView(R.id.swipe);
        sp.setSwipeEnabled(allowSwipe);
        helper.setText(R.id.tvTitle, deviceInfo.getShowName());
        helper.addOnClickListener(R.id.btReName);
        helper.addOnClickListener(R.id.deleteBtn);
        helper.addOnLongClickListener(R.id.tvTitle);
        helper.getView(R.id.sbOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DeviceInfo> data = getData();
                if (TextUtils.isEmpty(deviceInfo.getMac())) {
                    for (DeviceInfo info : data) {
                        info.setCheck(false);
                    }
                    //如果点击的是当前的设备 则反选开关设置 并且更新数据
                    deviceInfo.setCheck(!((SwitchButton) v).isChecked());
                    ToastUtils.showShortSafe(deviceInfo.getShowName().concat(deviceInfo.getCheck() ? "已打开" : "已关闭"));
                } else {
                    for (DeviceInfo info : data) {
                        info.setCheck(false);
                        if (info.getMac().equals(deviceInfo.getMac())) {
                            //如果点击的是当前的设备 则反选开关设置 并且更新数据
                            info.setCheck(!((SwitchButton) v).isChecked());
                            ToastUtils.showShortSafe(info.getShowName().concat(info.getCheck() ? "已打开" : "已关闭"));
                        }
                    }
                }
                DeviceDB.updateAll(data);
                notifyDataSetChanged();
            }
        });
        helper.setChecked(R.id.sbOpen, deviceInfo.getCheck());
    }

    public void setAllowSwipe(boolean allowSwipe) {
        this.allowSwipe = allowSwipe;
    }
}
