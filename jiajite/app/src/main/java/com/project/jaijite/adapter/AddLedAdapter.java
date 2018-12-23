package com.project.jaijite.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.entity.Light;

public class AddLedAdapter extends BaseQuickAdapter<Light,BaseViewHolder> {
    public AddLedAdapter() {
        super(R.layout.light_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, Light item) {
        helper.setImageResource(R.id.ivImage,item.getImageId());
        helper.setText(R.id.tvName,item.getName());
        helper.setText(R.id.tvPrice,"￥" + item.getPrice() + "元");
    }
}
