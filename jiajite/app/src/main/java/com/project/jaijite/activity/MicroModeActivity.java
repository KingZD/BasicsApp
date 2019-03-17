package com.project.jaijite.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.project.jaijite.R;
import com.project.jaijite.base.BaseVoiceActivity;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.gui.SwitchButton;
import com.project.jaijite.gui.VoiceLineView;
import com.project.jaijite.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MicroModeActivity extends BaseVoiceActivity implements Runnable {
    public static final String PARAM = "param";
    @BindView(R.id.voicLine)
    VoiceLineView voiceLineView;
    @BindView(R.id.sbTouch)
    SwitchButton switchButton;
    private MediaRecorder mMediaRecorder;
    private boolean isAlive = true;
    private boolean isAllowDrawWave = false;
    String rgb[];
    int mRgbIndex = 0;
    @BindViews({R.id.llMode1, R.id.llMode2, R.id.llMode3, R.id.llMode4})
    List<LinearLayout> llModes;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mMediaRecorder == null || !isAllowDrawWave) {
                cancel();
                return;
            }
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / 100;
            double db = 0;// 分贝
            //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
            //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
            //同时，也可以配置灵敏度sensibility
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            //只要有一个线程，不断调用这个方法，就可以使波形变化
            //主要，这个方法必须在ui线程中调用
            if (voiceLineView != null)
                voiceLineView.setVolume((int) (db));
            if (mRgbIndex == 3) {
                mRgbIndex = 0;
                int pp = (Integer.valueOf(rgb[0]) + Integer.valueOf(rgb[1]) + Integer.valueOf(rgb[2])) / 3;
                if (pp > 100)
                    pp = 100;
                String p = String.valueOf(pp);
                sendTask(rgb[0], rgb[1], rgb[2], p);
            }
            int voice = (int) (db * (mRgbIndex == 0 ? 2 : 1.5));
            if (voice <= 0) {
                rgb[mRgbIndex] = "01";
            } else if (voice < 10) {
                rgb[mRgbIndex] = "0" + voice;
            } else if (voice < 100) {
                rgb[mRgbIndex] = "" + voice;
            } else if (voice >= 100) {
                rgb[mRgbIndex] = "99";
            }
            mRgbIndex++;
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_micro_mode;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle("麦克风模式");
        updateUI();
        if (lightInfo != null)
            setTitleLeft(lightInfo.getName());
        rgb = new String[3];
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (!rxPermissions.isGranted(Manifest.permission.RECORD_AUDIO)) {
                            ToastUtils.showShortSafe("缺少录音权限");
                            return;
                        }

                        if (!rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            ToastUtils.showShortSafe("缺少读写权限");
                            return;
                        }
                        if (aBoolean) {
                            initAudio();
                        } else {
                            ToastUtils.showShort("申请权限失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("初始化麦克风失败:" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initAudio() {
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "jjt.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setMaxDuration(1000 * 60 * 10);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                isAllowDrawWave = isChecked;
            }
        });
        mMediaRecorder.start();
        Thread thread = new Thread(MicroModeActivity.this);
        thread.start();
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        isAlive = false;
        isAllowDrawWave = false;
        if (mMediaRecorder != null)
            mMediaRecorder.release();
        mMediaRecorder = null;
        super.onDestroy();
    }

    @Override
    public void run() {
        while (isAlive) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!isAllowDrawWave)
                cancel();
        }
    }

    @OnClick({R.id.llMode1, R.id.llMode2, R.id.llMode3, R.id.llMode4})
    void selectMode(View v) {
        switch (v.getId()) {
            case R.id.llMode1:
                lightInfo.setLedMGroup("0");
                break;
            case R.id.llMode2:
                lightInfo.setLedMGroup("1");
                break;
            case R.id.llMode3:
                lightInfo.setLedCGroup("0");
                break;
            case R.id.llMode4:
                lightInfo.setLedCGroup("1");
                break;
        }
        updateUI();
    }

    private void updateUI() {
        if (lightInfo == null) return;
        String ledCGroup = lightInfo.getLedCGroup();
        String ledMGroup = lightInfo.getLedMGroup();
        llModes.get(0).setBackgroundResource("0".equals(ledMGroup) ? R.color.darkGray : R.color.transparent);
        llModes.get(1).setBackgroundResource("1".equals(ledMGroup) ? R.color.darkGray : R.color.transparent);
        llModes.get(2).setBackgroundResource("0".equals(ledCGroup) ? R.color.darkGray : R.color.transparent);
        llModes.get(3).setBackgroundResource("1".equals(ledCGroup) ? R.color.darkGray : R.color.transparent);
        LightingDB.updateLight(lightInfo);
    }

}
