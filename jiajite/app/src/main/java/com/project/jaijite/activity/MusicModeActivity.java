package com.project.jaijite.activity;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.project.jaijite.R;
import com.project.jaijite.adapter.MusicAdapter;
import com.project.jaijite.base.BaseVoiceActivity;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.entity.MusicInfo;
import com.project.jaijite.event.OnPlayerEventListener;
import com.project.jaijite.greendao.db.LightingDB;
import com.project.jaijite.greendao.db.MusicDB;
import com.project.jaijite.util.AudioPlayerUtil;
import com.project.jaijite.util.LogUtils;
import com.project.jaijite.util.MusicUtil;
import com.project.jaijite.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MusicModeActivity extends BaseVoiceActivity {
    public static final String PARAM = "param";
    MusicAdapter mAdapter;
    @BindView(R.id.rlList)
    RecyclerView rlList;
    @BindView(R.id.pb)
    ProgressBar mProgressBar;
    @BindView(R.id.ivStart)
    ImageView ivStart;
    @BindView(R.id.ivSwitch)
    ImageView ivSwitch;
    int mPosition = -1;
    boolean isSingle = false;
    @BindViews({R.id.llMode1, R.id.llMode2, R.id.llMode3, R.id.llMode4})
    List<LinearLayout> llModes;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_mode;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle("我的音乐");
        updateUI();
        setTitleLeft(lightInfo.getName());
        mAdapter = new MusicAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPosition = position;
                AudioPlayerUtil.get().play(mPosition);
                mAdapter.setPlayIndex(mPosition);
            }
        });
        rlList.setLayoutManager(new LinearLayoutManager(this));
        rlList.setAdapter(mAdapter);
        initMusic();
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

//        int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @OnClick(R.id.ivSwitch)
    void switchMusic() {
        if (!isSingle) {
            isSingle = true;
            ivSwitch.setImageResource(R.mipmap.play_single);
        } else {
            isSingle = false;
            ivSwitch.setImageResource(R.mipmap.play_circle);
        }
    }

    @OnClick(R.id.ivPrev)
    void prev() {
        AudioPlayerUtil.get().prev(mPosition);
        mPosition--;
        if (mPosition < 0) {
            mPosition = mAdapter.getData().size() - 1;
        }
        mAdapter.setPlayIndex(mPosition);
        rlList.scrollToPosition(mPosition);
    }

    @OnClick(R.id.ivStart)
    void start() {
        if (mPosition < 0)
            mPosition = 0;
        AudioPlayerUtil.get().playPause(mPosition);
        mAdapter.setPlayIndex(mPosition);
        rlList.scrollToPosition(mPosition);
    }

    @OnClick(R.id.ivNext)
    void next() {
        nextMusic(true);
    }

    private void nextMusic(boolean isMustNext) {
        if (isSingle && !isMustNext) {
            AudioPlayerUtil.get().play(mPosition);
        } else {
            AudioPlayerUtil.get().next(mPosition);
            mPosition++;
            if (mPosition >= mAdapter.getData().size()) {
                mPosition = 0;
            }
            mAdapter.setPlayIndex(mPosition);
            if (rlList != null)
                rlList.scrollToPosition(mPosition);
        }
    }

    @OnClick(R.id.ivLight)
    void light() {

    }

    private void initMusicListener() {
        AudioPlayerUtil.get().setListener(new AudioPlayerUtil.OnVoiceFftListener() {
            @Override
            public void voice(String r, String g, String b, String p, String rgb) {
                sendTask(r, g, b, p);
            }

            @Override
            public void close() {
                cancel();
            }
        });
        AudioPlayerUtil.get().addOnPlayEventListener(new OnPlayerEventListener() {
            @Override
            public void onChange(MusicInfo music) {
                if (mProgressBar != null) {
                    mProgressBar.setMax((int) music.getDuration());
                    mProgressBar.setProgress((int) AudioPlayerUtil.get().getAudioPosition());
                }
            }

            @Override
            public void onPlayerStart() {
                if (ivStart != null)
                    ivStart.setImageResource(R.mipmap.music_play);
            }

            @Override
            public void onPlayerPause() {
                if (ivStart != null)
                    ivStart.setImageResource(R.mipmap.music_pause);
            }

            @Override
            public void onPublish(int progress) {
                if (mProgressBar != null) {
                    mProgressBar.setProgress(progress);
                    if (mProgressBar.getMax() == progress) {
                        if (ivStart != null)
                            ivStart.setImageResource(R.mipmap.music_pause);
                    }
                }
            }

            @Override
            public void onBufferingUpdate(int percent) {

            }

            @Override
            public void autoNext() {
                nextMusic(false);
            }
        });
    }

    private void initMusic() {
        showLoading();
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Observable
                                    .create(new ObservableOnSubscribe<List<MusicInfo>>() {
                                        @Override
                                        public void subscribe(ObservableEmitter<List<MusicInfo>> emitter) {
                                            emitter.onNext(MusicUtil.getMusicData(MusicModeActivity.this));
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<List<MusicInfo>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(List<MusicInfo> o) {
                                            initMusicListener();
                                            MusicDB.clearMusic();
                                            MusicDB.addAllMusic(o);
                                            AudioPlayerUtil.get().setMusicList(o);
                                            mAdapter.replaceData(o);
                                            hideLoading();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ToastUtils.showShort(e.getMessage());
                                            hideLoading();
                                        }

                                        @Override
                                        public void onComplete() {
                                            hideLoading();
                                        }
                                    });
                        } else {
                            ToastUtils.showShort("申请权限失败");
                            hideLoading();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("申请权限失败");
                        hideLoading();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        AudioPlayerUtil.get().stopPlayer();
        super.onDestroy();
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
