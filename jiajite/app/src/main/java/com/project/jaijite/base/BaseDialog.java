package com.project.jaijite.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.view.*;

import com.project.jaijite.R;
import com.project.jaijite.util.ScreenUtils;

public class BaseDialog extends Dialog {
    View mView;

    protected View getRootView() {
        return mView;
    }

    public BaseDialog(Context context) {
        super(context,R.style.dialog);
    }

    public BaseDialog(Context context,boolean fromButton) {
        super(context,R.style.Dialog_NoTitle);
        if (fromButton) {
            fromBottom();
        }
    }

    public BaseDialog(Context context,int themeResId) {
        super(context,themeResId);
    }

    @Override
    public void setContentView(@LayoutRes int resource) {
        mView = LayoutInflater.from(getContext()).inflate(resource, null);
        //设置SelectPicPopupWindow的View
        setContentView(mView);
        //设置全屏
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    private void fromBottom() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        localLayoutParams.width = ScreenUtils.getScreenWidth();
        localLayoutParams.y = 0;
        localLayoutParams.x = 0;
        onWindowAttributesChanged(localLayoutParams);
    }
}
