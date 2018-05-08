package com.funnywolf.simplemusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    private MusicControlBinder musicControlBinder;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        musicControlBinder = new MusicControlBinder();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicControlBinder;
    }

    public class MusicControlBinder extends Binder implements MusicControl{
        @Override
        public boolean play(String path) {
            Log.d(TAG, "play: " + path);
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void start() {
            mediaPlayer.start();
        }

        @Override
        public void pause() {
            mediaPlayer.pause();
        }

        @Override
        public void next() {

        }

        @Override
        public void prev() {

        }

        @Override
        public void changeMode(PlayMode mode) {

        }

        @Override
        public int getCurrentPosition() {
            return 0;
        }

        @Override
        public int getDuration() {
            return 0;
        }

        @Override
        public void seekTo(int msec) {

        }

        @Override
        public MusicItem getCurrentMusic() {
            return null;
        }
    }
}
