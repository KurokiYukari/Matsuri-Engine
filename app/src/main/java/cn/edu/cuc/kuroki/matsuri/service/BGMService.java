package cn.edu.cuc.kuroki.matsuri.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

import cn.edu.cuc.kuroki.matsuri.utils.ConfigUtils;
import cn.edu.cuc.kuroki.matsuri.utils.MainUtils;

/**
 * 控制 BGM 播放的 Service
 */
public class BGMService extends Service {
    private AssetManager assetManager;
    private MediaPlayer mediaPlayer;

    /**
     * @param intent intent
     * @return IBinder 一个 BGMBinder
     * @see BGMBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return new BGMBinder();
    }

    /**
     * init
     */
    @Override
    public void onCreate() {
        super.onCreate();
        assetManager = MainUtils.getContext().getAssets();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
    }

    /**
     * BGMService 的内部类，向外部提供操作 BGMService 的接口
     */
    public class BGMBinder extends Binder {
        /**
         * start to play an audio file as BGM
         * @param audioName audio file name in app assets
         */
        public void prepareAndStart(String audioName) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            ConfigUtils.setBGMVolume(MainUtils.getContext());
            try {
                AssetFileDescriptor fileDescriptor = assetManager.openFd(audioName);
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getStartOffset());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        }

        /**
         * is BGM playing
         * @return boolean
         */
        public boolean isPlaying(){
            return mediaPlayer.isPlaying();
        }

        /**
         * start BGM
         */
        public void start() {
            mediaPlayer.start();
        }

        /**
         * pause BGM
         */
        public void pause() {
            mediaPlayer.pause();
        }


        /**
         * get BGM duration (ms)
         * @return int
         */
        public int getDuration(){
            return mediaPlayer.getDuration();
        }


        /**
         * get current BGM position (ms)
         * @return int
         */
        public int getCurrentPosition(){
            return mediaPlayer.getCurrentPosition();
        }


        /**
         * seek to new position
         * @param mesc position seeking to
         */
        public void seekTo(int mesc){
            mediaPlayer.seekTo(mesc);
        }

        /**
         * set BGM volume
         * @param volume volume
         */
        public void setVolume(float volume) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
}
