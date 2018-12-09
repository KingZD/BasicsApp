package com.project.jaijite.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.bean.SceneBean;
import com.squareup.picasso.Picasso;

public class SceneAdapter extends BaseQuickAdapter<SceneBean, BaseViewHolder> {

    public SceneAdapter() {
        super(R.layout.item_scene);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SceneBean item) {
//        Picasso.with(helper.itemView.getContext())
//                .load(item.getResId())
//                .into((ImageView) helper.getView(R.id.ivIcon));
        helper.setImageResource(R.id.ivIcon,item.getResId());
        helper.setText(R.id.tvName, item.getName());
    }
}
