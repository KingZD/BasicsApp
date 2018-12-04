package com.project.jaijite.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.project.jaijite.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingHeaderView extends RelativeLayout {
    @BindView(R.id.sbTouch)
    SwitchButton switchButton;
    Unbinder bind;

    public SettingHeaderView(Context context) {
        super(context);
        initView();
    }

    public SettingHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.header_setting, this);
        bind = ButterKnife.bind(this, inflate);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                mListener.touch(view, isChecked);
            }
        });
    }

    //功能介绍
    @OnClick(R.id.tvDesc)
    void functionIntroduction() {
        mListener.functionIntroduction();
    }

    @OnClick(R.id.tvAdd)
    void addDevice() {
        mListener.addDevice();
    }

    public void onDstory() {
        if (bind != null)
            bind.unbind();
    }

    public interface OnHeaderListener {
        void functionIntroduction();

        void touch(SwitchButton view, boolean isChecked);

        void addDevice();
    }

    private OnHeaderListener mListener;

    public void setOnHeaderListener(OnHeaderListener listener) {
        mListener = listener;
    }

    public int getViewCount(){
        return 3;
    }
}
