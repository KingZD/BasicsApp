package com.project.jaijite.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.jaijite.R;
import com.project.jaijite.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseTitleActivity extends AppCompatActivity {
    Unbinder bind;
    @BindView(R.id.btLeft)
    Button btLeft;
    @BindView(R.id.btRight)
    Button btRight;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        setContentView(rootView);
        //添加新主体容器
        View contentView = LayoutInflater.from(this).inflate(getLayoutId(), null);
        rootView.addView(contentView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        layoutParams.weight = 1;
        //绑定ui
        bind = ButterKnife.bind(this, rootView);
        initView();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    /**
     * 设置左标题
     *
     * @param word 文字
     */
    protected void setTitleLeft(String word) {
        if (btLeft != null) {
            btLeft.setText(word);
        }
    }

    /**
     * 设置左标题
     *
     * @param word  文字
     * @param resId 资源
     */
    protected void setTitleLeft(String word, int resId) {
        if (btLeft != null) {
            btLeft.setText(word);
            btLeft.setCompoundDrawablesRelativeWithIntrinsicBounds
                    (resId == -1 ? null : ContextCompat.getDrawable(this, resId),
                            null, null, null);
        }
    }

    /**
     * 设置标题
     *
     * @param word 文字
     */
    protected void setTvTitle(String word) {
        if (tvTitle != null) {
            tvTitle.setText(word);
        }
    }

    /**
     * 设置右标题
     *
     * @param word 文字
     */
    protected void setTitleRight(String word) {
        if (btRight != null) {
            btRight.setText(word);
        }
    }

    /**
     * 设置右标题
     *
     * @param word  文字
     * @param resId 资源
     */
    protected void setTitleRight(String word, int resId) {
        if (btRight != null) {
            btRight.setText(word);
            btRight.setCompoundDrawablesRelativeWithIntrinsicBounds
                    (null,
                            null,
                            resId == -1 ? null : ContextCompat.getDrawable(this, resId),
                            null);
        }
    }

    protected void showLoading() {
        showLoading("");
    }

    protected void showLoading(String content) {
        showLoading(content, null);
    }

    protected void showLoading(String content, DialogInterface.OnCancelListener cancelListener) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        if (cancelListener != null)
            loadingDialog.setOnCancelListener(cancelListener);
        loadingDialog.setContent(content);
        loadingDialog.show();
    }

    protected void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }

    private void dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        dismissLoading();
        if (bind != null)
            bind.unbind();
    }
}
