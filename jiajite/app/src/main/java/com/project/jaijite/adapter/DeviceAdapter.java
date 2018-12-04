package com.project.jaijite.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.entity.DeviceInfo;

public class DeviceAdapter extends BaseQuickAdapter<DeviceInfo, BaseViewHolder> {

    public DeviceAdapter() {
        super(R.layout.item_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceInfo item) {
        helper.setText(R.id.tvTitle, item.getName());
        helper.addOnClickListener(R.id.btReName);
        helper.addOnClickListener(R.id.deleteBtn);
    }
}
