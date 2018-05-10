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
        private PlayMode playMode = PlayMode.LIST_LOOP_MODE;

        @Override
        public void play(List<MusicItem> list, int position) {
            if (list == null || position < 0 || position >= list.size())
                return;
            mediaPlayer.reset();
            mMusicList = list;
            currentPosition = position;
            try {
                MusicItem music = mMusicList.get(currentPosition);
                music.currentTime = 0;
                mediaPlayer.setDataSource(music.path);
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
        public void pause() {
            mediaPlayer.pause();
        }

        @Override
        public void next() {
            if (mMusicList == null)
                return;
            switch (playMode) {
                case LIST_LOOP_MODE:
                    break;
                case RANDOM_MODE:
                    break;
                case SINGLE_LOOP_MODE:
                    break;
                default:
                    break;
            }
            if(currentPosition < mMusicList.size() - 1)
                currentPosition += 1;
            else
                currentPosition = 0;
            play(mMusicList, currentPosition);
            start();
        }

        @Override
        public void prev() {
            if (mMusicList == null)
                return;
            switch (playMode) {
                case LIST_LOOP_MODE:
                    playMode = PlayMode.RANDOM_MODE;
                    break;
                case RANDOM_MODE:
                    playMode = PlayMode.SINGLE_LOOP_MODE;
                    break;
                case SINGLE_LOOP_MODE:
                    playMode = PlayMode.LIST_LOOP_MODE;
                    break;
                default:

            }
            if(currentPosition != 0)
                currentPosition -= 1;
            else
                currentPosition = mMusicList.size() - 1;
            play(mMusicList, currentPosition);
            start();
        }

        @Override
        public PlayMode getMode() {
            return playMode;
        }

        @Override
        public void setMode(PlayMode mode) {
            playMode = mode;
        }

        @Override
        public void seekTo(int msec) {
            MusicItem music = mMusicList.get(currentPosition);
            music.currentTime = msec * music.durationInt / 100;
            mediaPlayer.seekTo(music.currentTime);
        }

        @Override
        public MusicItem getCurrentMusic() {
            if(mMusicList == null)
                return null;
            MusicItem music = mMusicList.get(currentPosition);
            music.currentTime = mediaPlayer.getCurrentPosition();
            return music;
        }
    }
}
