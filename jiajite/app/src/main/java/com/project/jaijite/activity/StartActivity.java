package com.project.jaijite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.project.jaijite.R;
import com.project.jaijite.base.BaseActivity;
import com.project.jaijite.util.ToastUtils;


/**
 * Created by feijie.xfj on 17/11/27.
 */
//launcher页面不可以singleTask`
public class StartActivity extends BaseActivity {
    private static final String TAG = "StartActivity";

    private CountDownTimer countDownTimer;
    private Handler mH = new Handler();

    @Override
    public int getLayoutId() {
        return R.layout.start_activity;
    }

    @Override
    public void initView() {
        countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (LoginBusiness.isLogin()) {
                    startActivity(new Intent(StartActivity.this,MainActivity.class));
                } else {
                    LoginBusiness.login(new ILoginCallback() {
                        @Override
                        public void onLoginSuccess() {
                            mH.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(StartActivity.this,MainActivity.class));
                                }
                            }, 0);

                        }


                        @Override
                        public void onLoginFailed(int i, String s) {
                            ToastUtils.showShortSafe("登录失败 :" + s);
                        }
                    });
                }
                finish();
            }
        };
        countDownTimer.start();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = null;
        super.onDestroy();
    }

}
