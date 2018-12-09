package com.project.jaijite.activity;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.project.jaijite.R;
import com.project.jaijite.adapter.MusicAdapter;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.entity.MusicInfo;
import com.project.jaijite.event.OnPlayerEventListener;
import com.project.jaijite.greendao.db.MusicDB;
import com.project.jaijite.util.AudioPlayerUtil;
import com.project.jaijite.util.MusicUtil;
import com.project.jaijite.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MusicModeActivity extends BaseTitleActivity {
    public static final String PARAM = "param";
    private LightInfo lightInfo;
    MusicAdapter mAdapter;
    @BindView(R.id.rlList)
    RecyclerView rlList;
    @BindView(R.id.pb)
    ProgressBar mProgressBar;
    @BindView(R.id.ivStart)
    ImageView ivStart;
    int mPosition = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_mode;
    }

    @Override
    public void initView() {
        lightInfo = (LightInfo) getIntent().getSerializableExtra(PARAM);
        setTvTitle("我的音乐");
        setTitleLeft(lightInfo.getName());
        mAdapter = new MusicAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mPosition = position;
                AudioPlayerUtil.get().play(mPosition);
            }
        });
        rlList.setLayoutManager(new LinearLayoutManager(this));
        rlList.setAdapter(mAdapter);
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
                if (mProgressBar != null)
                    mProgressBar.setProgress(progress);
            }

            @Override
            public void onBufferingUpdate(int percent) {

            }
        });
        initMusic();
    }

    @OnClick(R.id.ivSwitch)
    void switchMucin() {

    }

    @OnClick(R.id.ivPrev)
    void prev() {
        AudioPlayerUtil.get().prev(mPosition);
        mPosition--;
    }

    @OnClick(R.id.ivStart)
    void start() {
        AudioPlayerUtil.get().playPause(mPosition);
    }

    @OnClick(R.id.ivNext)
    void next() {
        AudioPlayerUtil.get().next(mPosition);
        mPosition++;
    }

    @OnClick(R.id.ivLight)
    void light() {

    }

    private void initMusic() {
        showLoading();
        Disposable subscribe = new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Observable
                                    .create(new ObservableOnSubscribe<List<MusicInfo>>() {
                                        @Override
                                        public void subscribe(ObservableEmitter<List<MusicInfo>> emitter) throws Exception {
                                            emitter.onNext(MusicUtil.getMusicData(MusicModeActivity.this));
                                        }
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<MusicInfo>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(List<MusicInfo> o) {
                                    MusicDB.clearMusic();
                                    MusicDB.addAllMusic(o);
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
                });
    }

    @OnClick(R.id.btLeft)
    void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayerUtil.get().stopPlayer();
    }
}
