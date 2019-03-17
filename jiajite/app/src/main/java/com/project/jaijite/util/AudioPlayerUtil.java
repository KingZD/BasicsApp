package com.project.jaijite.util;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;

import com.project.jaijite.KittApplication;
import com.project.jaijite.entity.MusicInfo;
import com.project.jaijite.event.AudioFocusManager;
import com.project.jaijite.event.OnPlayerEventListener;
import com.project.jaijite.greendao.db.MusicDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hzwangchenyan on 2018/1/26.
 */
public class AudioPlayerUtil {
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private static final long TIME_UPDATE = 300L;

    private Context context;
    private AudioFocusManager audioFocusManager;
    private MediaPlayer mediaPlayer;
    Visualizer mVisualizer;
    Equalizer mEqualizer;
    private Handler handler;
    private IntentFilter noisyFilter;
    private List<MusicInfo> musicList;
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();
    private int state = STATE_IDLE;
    private String db = "0";//采样的最大值 为byte 最大值

    public static AudioPlayerUtil get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioPlayerUtil instance = new AudioPlayerUtil();
    }

    private AudioPlayerUtil() {
        init(KittApplication.getApplication());
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        musicList = MusicDB.getAllMusic();
        audioFocusManager = new AudioFocusManager(context);
        mediaPlayer = new MediaPlayer();
        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        handler = new Handler(Looper.getMainLooper());
        noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVisualizer.setEnabled(false);
                mEqualizer.setEnabled(false);
//                next(0);
                for (OnPlayerEventListener listener : listeners) {
                    if (state != STATE_PAUSE)
                        listener.autoNext();
                }
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (isPreparing()) {
                    startPlayer();
                }
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onBufferingUpdate(percent);
                }
            }
        });
    }

    public void addOnPlayEventListener(OnPlayerEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnPlayEventListener(OnPlayerEventListener listener) {
        listeners.remove(listener);
    }

    public void addAndPlay(MusicInfo music) {
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            MusicDB.insert(music);
            position = musicList.size() - 1;
        }
        play(position);
    }

    public void play(int position) {
        if (musicList.isEmpty()) {
            return;
        }
        mVisualizer.setEnabled(false);
        mEqualizer.setEnabled(false);
        if (position < 0) {
            position = musicList.size() - 1;
        } else if (position >= musicList.size()) {
            position = 0;
        }

        MusicInfo music = getPlayMusic(position);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getPath());
            mediaPlayer.prepareAsync();
            mediaPlayer.setVolume(1, 1);
            final int maxCR = Visualizer.getMaxCaptureRate();
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            mVisualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        public void onWaveFormDataCapture(Visualizer visualizer,
                                                          byte[] waveform, int samplingRate) {
                            long v = 0;
                            for (int i = 0; i < waveform.length; i++) {
                                v += Math.pow(waveform[i], 2);
                            }
                            double volume = 10 * Math.log10(v / (double) waveform.length);
                            LogUtils.i("vC_wave", volume);
                            db = String.valueOf((int) volume);
                        }

                        public void onFftDataCapture(Visualizer visualizer,
                                                     byte[] fft, int samplingRate) {
//                            mEqualizer.getCurrentPreset();
                            updateVisualizer(fft);
                        }
                    }, maxCR / 2, true, true);
            mVisualizer.setEnabled(true);
            mEqualizer.setEnabled(true);
            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                listener.onChange(music);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showShort("当前歌曲无法播放");
        }
    }

    private int mSpectrumNum = 12;

    public void updateVisualizer(byte[] fft) {
        byte[] model = new byte[fft.length / 2 + 1];  //
        model[0] = (byte) Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mSpectrumNum; ) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }
        byte[] mBytes = model;
        //绘制频谱
        String rgb[] = new String[3];
        rgb[0] = "01";
        rgb[1] = "01";
        rgb[2] = "01";
        int mIndex = 0;
        int maxV = 0;
        for (int i = 0; i < mSpectrumNum; i++) {
            if (mBytes[i] < 0) {
                mBytes[i] = 127;
            }
            int value = mBytes[i] * 99 / 127;
            if (i == 1 || i == 5 || i == 9) {
                if (value <= 0) {
                    rgb[mIndex] = "01";
                } else if (value < 10) {
                    rgb[mIndex] = "0" + value;
                } else if (value < 100) {
                    rgb[mIndex] = "" + value;
                } else if (value >= 100) {
                    rgb[mIndex] = "99";
                }
                if (maxV < value)
                    maxV = value;
                mIndex++;
            }
        }

//        if (listener != null)
//            listener.voice(rgb[0], rgb[1], rgb[2], String.valueOf(sum / 3), rgb[0].concat(rgb[1]).concat(rgb[2]));
        if (listener != null)
            listener.voice(rgb[0], rgb[1], rgb[2], String.valueOf(maxV), rgb[0].concat(rgb[1]).concat(rgb[2]));
        if (isPausing() || isIdle())
            if (listener != null)
                listener.close();
    }

    public void playPause(int position) {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(position);
        }
    }

    public void startPlayer() {
        if (!isPreparing() && !isPausing()) {
            return;
        }
        if (mVisualizer != null)
            mVisualizer.setEnabled(true);
        if (mEqualizer != null)
            mEqualizer.setEnabled(true);
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable);
            for (OnPlayerEventListener listener : listeners) {
                listener.onPlayerStart();
            }
        }
    }

    public void pausePlayer() {
        pausePlayer(true);
    }

    public void pausePlayer(boolean abandonAudioFocus) {
        if (mVisualizer != null)
            mVisualizer.setEnabled(false);
        if (mEqualizer != null)
            mEqualizer.setEnabled(false);
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus();
        }
        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerPause();
        }
    }

    public void stopPlayer() {
        if (isIdle()) {
            return;
        }

        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    public void prev(int currPosition) {
        if (musicList.isEmpty()) {
            return;
        }
        play(currPosition - 1);
    }

    public void next(int currPosition) {
        if (musicList.isEmpty()) {
            return;
        }
        play(currPosition + 1);
    }


    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            for (OnPlayerEventListener listener : listeners) {
                listener.onPublish(msec);
            }
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public MusicInfo getPlayMusic(int position) {
        if (musicList.isEmpty()) {
            return null;
        }
        return musicList.get(position);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicInfo> musicList) {
        this.musicList = musicList;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public OnVoiceFftListener listener;

    public interface OnVoiceFftListener {
        void voice(String r, String g, String b, String p, String rgb);

        void close();
    }

    public void setListener(OnVoiceFftListener listener) {
        this.listener = listener;
    }
}
