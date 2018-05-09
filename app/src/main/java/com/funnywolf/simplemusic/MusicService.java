package com.funnywolf.simplemusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        private List<MusicItem> mMusicList;
        private int currentPosition;

        @Override
        public void play(List<MusicItem> list, int position) {
            mMusicList = list;
            currentPosition = position;
            try {
                mediaPlayer.setDataSource(mMusicList.get(currentPosition).getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void start() {
            mediaPlayer.start();
        }

        @Override
        public void stop() {
            mediaPlayer.stop();
            mediaPlayer.reset();
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
