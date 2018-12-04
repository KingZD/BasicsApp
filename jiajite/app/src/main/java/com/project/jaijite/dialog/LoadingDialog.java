package com.project.jaijite.dialog;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.project.jaijite.R;
import com.project.jaijite.base.BaseDialog;


public class LoadingDialog extends BaseDialog {
    private SpinKitView spin_kit;
    private TextView tvContent;

    public LoadingDialog(Context context) {
        super(context);
        initViews();
    }

    public LoadingDialog(Context context, boolean fromButton) {
        super(context, fromButton);
        initViews();
    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        initViews();
    }

    public LoadingDialog showDialog(Context context,String message ) {
        LoadingDialog dialog = new LoadingDialog(context, R.style.dialog);
        dialog.setContent(message);
        dialog.show();
        return dialog;
    }

    private void initViews() {
        setContentView(R.layout.view_loading);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        tvContent = findViewById(R.id.tvContent);
        spin_kit = findViewById(R.id.spin_kit);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setContent(String content) {
        if (TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(content);
        }
    }

    public void dismiss(Long delayMills) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },delayMills);
    }
}
