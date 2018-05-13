package com.funnywolf.simplemusic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    public static final String ACTION_PREV = "com.funnywolf.simplemusic.MusicService.PREV";
    public static final String ACTION_START_PAUSE =
            "com.funnywolf.simplemusic.MusicService.START_PAUSE";
    public static final String ACTION_NEXT = "com.funnywolf.simplemusic.MusicService.NEXT";

    private static final String TAG = "MusicService";
    private static final String NOTIFICATION_ID = "MusicService";

    private MusicControlBinder mMusicControlBinder;
    private RemoteViews mRemoteViews;
    private Notification mNotification;
    private NotificationControlReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mMusicControlBinder = new MusicControlBinder();

        mReceiver = new NotificationControlReceiver(mMusicControlBinder);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PREV);
        intentFilter.addAction(ACTION_START_PAUSE);
        intentFilter.addAction(ACTION_NEXT);
        registerReceiver(mReceiver, intentFilter);

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, "music",
                NotificationManager.IMPORTANCE_MIN);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);

        mRemoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_PREV), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.panel_prev, pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_START_PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.panel_start_stop, pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.panel_next, pendingIntent);

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        mNotification = new NotificationCompat.Builder(this, NOTIFICATION_ID)
                .setContent(mRemoteViews)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setSound(null)
                .build();
        startForeground(1, mNotification);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void updateNotification() {
        MusicItem music = null;
        boolean playing = false;

        if(mMusicControlBinder != null) {
            music = mMusicControlBinder.getCurrentMusic();
            playing = mMusicControlBinder.isPlaying();
        }
        if(music != null) {
            mRemoteViews.setTextViewText(R.id.panel_title, music.title);
            mRemoteViews.setTextViewText(R.id.panel_artist, music.artist);
        }else {
            mRemoteViews.setTextViewText(R.id.panel_title, "------");
            mRemoteViews.setTextViewText(R.id.panel_artist, "------");
        }
        if(playing) {
            mRemoteViews.setInt(R.id.panel_start_stop, "setBackgroundResource",
                    R.drawable.pause);
        }else {
            mRemoteViews.setInt(R.id.panel_start_stop, "setBackgroundResource",
                    R.drawable.start);
        }
        startForeground(1, mNotification);
    }

    @Override
    public IBinder onBind(Intent intent) {
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
            updateNotification();
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            currentPosition = updatePosition(playMode, mMusicList.size(), currentPosition,
                    true);
            play(mMusicList, currentPosition);
            start();
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
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                updateNotification();
            }
        }

        @Override
        public void start() {
            if (!isReady || mMusicList == null)
                return;
            isPlaying = true;
            mMediaPlayer.start();
            updateNotification();
        }

        @Override
        public void pause() {
            if (!isReady || mMusicList == null)
                return;
            isPlaying = false;
            mMediaPlayer.pause();
            updateNotification();
        }

        @Override
        public void next() {
            if (!isReady || mMusicList == null)
                return;
            currentPosition = updatePosition(playMode, mMusicList.size(), currentPosition,
                    true);
            play(mMusicList, currentPosition);
            start();
        }

        @Override
        public void prev() {
            if (!isReady || mMusicList == null)
                return;
            currentPosition = updatePosition(playMode, mMusicList.size(), currentPosition,
                    false);
            play(mMusicList, currentPosition);
            start();
        }

        @Override
        public void setMode(PlayMode mode) {
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

        private int updatePosition(PlayMode mode, int size, int p, boolean next) {
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

    class NotificationControlReceiver extends BroadcastReceiver {

        private MusicControl controller;

        public NotificationControlReceiver(MusicControl musicControl) {
            super();
            controller = musicControl;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action;
            if(intent == null || (action = intent.getAction()) == null){
                return;
            }
            switch(action){
                case ACTION_PREV:
                    controller.prev();
                    break;
                case ACTION_START_PAUSE:
                    if(controller.isPlaying())
                        controller.pause();
                    else
                        controller.start();
                    break;
                case ACTION_NEXT:
                    controller.next();
                    break;
                default:
                    break;
            }
        }
    }
}
