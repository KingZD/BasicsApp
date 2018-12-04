package com.project.jaijite.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.entity.LightInfo;
import com.squareup.picasso.Picasso;

public class AddLightAdapter extends BaseQuickAdapter<LightInfo, BaseViewHolder> {

    public AddLightAdapter() {
        super(R.layout.item_add_light);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final LightInfo item) {
        Picasso.with(helper.itemView.getContext())
                .load(R.mipmap.led_opend)
                .into((ImageView) helper.getView(R.id.ivIcon));
        helper.setChecked(R.id.ledCheckBox, item.getIsCheck());
        helper.setText(R.id.ledNameTv, item.getName());
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setIsCheck(!item.getIsCheck());
                helper.setChecked(R.id.ledCheckBox, item.getIsCheck());
                item.setIsDelete(item.getIsCheck() ? 0 : 1);
            }
        });
    }
}
