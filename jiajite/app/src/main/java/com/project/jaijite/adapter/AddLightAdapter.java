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
                .load(R.mipmap.led_offed)
                .into((ImageView) helper.getView(R.id.ivIcon));
        helper.setText(R.id.ledNameTv, item.getName());
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setIsDelete(item.getIsDelete() == 1 ? 0 : 1);
                helper.setChecked(R.id.ledCheckBox, item.getIsDelete() == 0);
            }
        });
    }
}
