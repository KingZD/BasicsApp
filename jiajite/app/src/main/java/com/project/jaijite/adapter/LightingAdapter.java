package com.project.jaijite.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.entity.LightInfo;
import com.squareup.picasso.Picasso;

public class LightingAdapter extends BaseQuickAdapter<LightInfo, BaseViewHolder> {

    public LightingAdapter() {
        super(R.layout.item_lighting);
    }

    @Override
    protected void convert(BaseViewHolder helper, LightInfo light) {
        Picasso.with(helper.itemView.getContext())
                .load(R.mipmap.led_opend)
                .into((ImageView) helper.getView(R.id.ledPowerBtn));
        helper.setText(R.id.ledNameTv, light.getName());
        helper.setText(R.id.ledLightTv, "亮度：" + light.getLight_level() + "%");
        helper.setText(R.id.ledTempTv, "色温：" + light.getColor_temp() + "k");
        String openLedTv = "定时开灯：" + (TextUtils.equals(light.getTime_on(), "00:00") ? "关" : "开");
        helper.setText(R.id.timingOpenLedTv, openLedTv);
        String closeLedTv = "定时关灯：" + (TextUtils.equals(light.getTime_off(), "00:00") ? "关" : "开");
        helper.setText(R.id.timingCloseLedTv, closeLedTv);
        String delayLedTv = "延时关灯：" + (TextUtils.equals(light.getDelay(), "00") ? "关" : "开");
        helper.setText(R.id.delayCloseLedTv, delayLedTv);
        helper.addOnClickListener(R.id.btAddLed);
        helper.addOnClickListener(R.id.btReName);
        helper.addOnClickListener(R.id.deleteBtn);
        helper.addOnClickListener(R.id.llBody);
    }
}
