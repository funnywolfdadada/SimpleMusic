package com.funnywolf.simplemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private static final String NOTIFICATION_ID = "MusicService";

    private MusicControlBinder mMusicControlBinder;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;

    @Override
    public void onCreate() {
        super.onCreate();

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, "music",
                NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
        mRemoteViews.setTextViewText(R.id.panel_title, "44445454");
        mRemoteViews.setInt(R.id.panel_start_stop,"setBackgroundResource",
                R.drawable.pause);
        Notification nt = new NotificationCompat.Builder(this, NOTIFICATION_ID)
                .setContentTitle("SimpleMusic")
                .setContent(mRemoteViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi)
                .build();
        startForeground(1, nt);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(mMusicControlBinder == null)
            mMusicControlBinder = new MusicControlBinder();
        return mMusicControlBinder;
    }

    private class MusicControlBinder extends Binder
            implements MusicControl, MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener{

        private MediaPlayer mMediaPlayer;
        private List<MusicItem> mMusicList;

        private boolean isReady = false;
        private boolean isPlaying = false;
        private PlayMode playMode = PlayMode.LIST_LOOP_MODE;
        private int currentPosition = 0;

        MusicControlBinder() {
            super();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        }

        /**
         * for MediaPlayer's listeners
         */
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            isReady = false;
            isPlaying = false;
            mMediaPlayer.reset();
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            currentPosition = updatePosition(playMode, mMusicList.size(), currentPosition,
                    true);
            play(mMusicList, currentPosition);
        }

        /**
         * for MusicControl interface
         */
        @Override
        public void play(List<MusicItem> list, int position) {
            isReady = false;
            isPlaying = false;
            mMediaPlayer.reset();
            if (list == null || position < 0 || position >= list.size()) {
                return;
            }
            mMusicList = list;
            currentPosition = position;
            try {
                MusicItem music = mMusicList.get(currentPosition);
                music.currentTime = 0;
                mMediaPlayer.setDataSource(music.path);
                mMediaPlayer.prepare();
                isReady = true;
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void start() {
            if (!isReady || mMusicList == null)
                return;
            isPlaying = true;
            mMediaPlayer.start();
        }

        @Override
        public void pause() {
            if (!isReady || mMusicList == null)
                return;
            isPlaying = false;
            mMediaPlayer.pause();
        }

        @Override
        public void next() {
            if (!isReady || mMusicList == null)
                return;
            currentPosition = updatePosition(playMode, mMusicList.size(), currentPosition,
                    true);
            play(mMusicList, currentPosition);
        }

        @Override
        public void prev() {
            if (!isReady || mMusicList == null)
                return;
            currentPosition = updatePosition(playMode, mMusicList.size(), currentPosition,
                    false);
            play(mMusicList, currentPosition);
        }

        @Override
        public void setMode(PlayMode mode) {
            if(isReady)
                playMode = mode;
        }

        @Override
        public void seekTo(int msec) {
            if (!isReady || mMusicList == null)
                return;
            MusicItem music = mMusicList.get(currentPosition);
            music.currentTime = msec * music.durationInt / 100;
            mMediaPlayer.seekTo(music.currentTime);
        }

        @Override
        public PlayMode getMode() {
            return playMode;
        }

        @Override
        public MusicItem getCurrentMusic() {
            if (!isReady || mMusicList == null)
                return null;
            MusicItem music = mMusicList.get(currentPosition);
            music.currentTime = mMediaPlayer.getCurrentPosition();
            return music;
        }

        @Override
        public boolean isPlaying() {
            return isPlaying;
        }

        public int updatePosition(PlayMode mode, int size, int p, boolean next) {
            switch (mode) {
                case LIST_LOOP_MODE:
                    p = next ? p + 1 : p - 1;
                    if(p < 0)
                        p = size - 1;
                    else if(p >= size)
                        p = 0;
                    break;
                case RANDOM_MODE:
                    p = (p + (int)(Math.random() * size)) % size;
                    break;
                case SINGLE_LOOP_MODE:
                    break;
                default:
                    break;
            }
            return p;
        }
    }
}
