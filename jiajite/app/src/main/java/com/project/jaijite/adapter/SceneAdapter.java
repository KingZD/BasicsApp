package com.project.jaijite.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.bean.SceneBean;

public class SceneAdapter extends BaseQuickAdapter<SceneBean, BaseViewHolder> {
    //0表示情景模式关闭 所以选择的下标+1
    int selectIndex = 0;

    public SceneAdapter() {
        super(R.layout.item_scene);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final SceneBean item) {
//        Picasso.with(helper.itemView.getContext())
//                .load(item.getResId())
//                .into((ImageView) helper.getView(R.id.ivIcon));
        helper.setImageResource(R.id.ivIcon, item.getResId());
        helper.setText(R.id.tvName, item.getName());
        helper.addOnClickListener(R.id.llRoot);
        helper.getView(R.id.llRoot).setBackgroundResource(selectIndex == (helper.getAdapterPosition() + 1) ?
                R.color.darkGray : R.color.transparent);
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }
}
