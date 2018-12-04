package com.project.jaijite.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.project.jaijite.R;
import com.project.jaijite.dialog.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    Unbinder bind;
    @Nullable
    @BindView(R.id.btLeft)
    Button btLeft;
    @Nullable
    @BindView(R.id.btRight)
    Button btRight;
    @Nullable
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        bind = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        if (bind != null)
            bind.unbind();
    }

    /**
     * 设置左标题
     * @param word 文字
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
     * @param word 文字
     */
    protected void setTvTitle(String word){
        if (tvTitle != null) {
            tvTitle.setText(word);
        }
    }

    /**
     * 设置右标题
     * @param word 文字
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
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        loadingDialog.show();
    }

    protected void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }

    private void dismissLoading(){
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public abstract int getLayoutId();

    public abstract void initView();
}
