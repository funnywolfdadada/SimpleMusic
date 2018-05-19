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
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.funnywolf.simplemusic.Database.MusicItem;
import com.funnywolf.simplemusic.Database.MusicList;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    public static final String ACTION_PREV = "com.funnywolf.simplemusic.MusicService.PREV";
    public static final String ACTION_START_PAUSE =
            "com.funnywolf.simplemusic.MusicService.START_PAUSE";
    public static final String ACTION_NEXT = "com.funnywolf.simplemusic.MusicService.NEXT";

    private static final String NOTIFICATION_ID = "MusicService";

    private MusicControlBinder mMusicControlBinder = new MusicControlBinder();
    private RemoteViews mRemoteViews;
    private Notification mNotification;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action;
            if(mMusicControlBinder == null
                    || intent == null || (action = intent.getAction()) == null){
                return;
            }
            switch(action){
                case ACTION_PREV:
                    mMusicControlBinder.prev();
                    break;
                case ACTION_START_PAUSE:
                    if(mMusicControlBinder.isPlaying())
                        mMusicControlBinder.pause();
                    else
                        mMusicControlBinder.play();
                    break;
                case ACTION_NEXT:
                    mMusicControlBinder.next();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

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
        mRemoteViews.setOnClickPendingIntent(R.id.panel_play_pause, pendingIntent);
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
    public IBinder onBind(Intent intent) {
        return mMusicControlBinder;
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
            mRemoteViews.setTextViewText(R.id.panel_title, music.getTitle());
            mRemoteViews.setTextViewText(R.id.panel_artist, music.getArtist());
        }else {
            mRemoteViews.setTextViewText(R.id.panel_title, "------");
            mRemoteViews.setTextViewText(R.id.panel_artist, "------");
        }
        if(playing) {
            mRemoteViews.setInt(R.id.panel_play_pause, "setBackgroundResource",
                    R.drawable.pause);
        }else {
            mRemoteViews.setInt(R.id.panel_play_pause, "setBackgroundResource",
                    R.drawable.start);
        }
        startForeground(1, mNotification);
    }

    private class MusicControlBinder extends Binder
            implements MusicControl, MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {

        private MediaPlayer mMediaPlayer;

        private MusicList<MusicItem> mMusicList;
        private int mCurrentPosition = 0;
        private MusicItem mCurrentMusic = null;
        private boolean isPlaying = false;
        private PlayMode mPlayMode = PlayMode.LIST_LOOP_MODE;

        MusicControlBinder() {
            super();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        }

        private void updatePosition(boolean next) {
            int size = mMusicList.size();
            switch (mPlayMode) {
                case LIST_LOOP_MODE:
                    mCurrentPosition = next ? mCurrentPosition + 1 : mCurrentPosition - 1;
                    if(mCurrentPosition < 0)
                        mCurrentPosition = size - 1;
                    else if(mCurrentPosition >= size)
                        mCurrentPosition = 0;
                    break;
                case RANDOM_MODE:
                    mCurrentPosition = (mCurrentPosition + (int)(Math.random() * size)) % size;
                    break;
                case SINGLE_LOOP_MODE:
                    break;
                default:
                    break;
            }
        }

        /**
         * for MusicControl interface
         */
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            mCurrentMusic = null;
            isPlaying = false;
            mMediaPlayer.reset();
            updateNotification();
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            next();
        }

        @Override
        public void setMusicList(MusicList<MusicItem> list) {
            mMusicList = list;
        }

        @Override
        public void setCurrentPosition(int position) {
            if(mMusicList == null)
                return;
            if(position >= 0 && position < mMusicList.size())
                mCurrentPosition = position;
        }

        @Override
        public int getCurrentPosition() {
            return mCurrentPosition;
        }

        @Override
        public MusicItem getCurrentMusic() {
            if(mCurrentMusic != null)
                mCurrentMusic.setCurrentTime(mMediaPlayer.getCurrentPosition());
            return mCurrentMusic;
        }

        @Override
        public void prepare() {
            isPlaying = false;
            mCurrentMusic = null;
            mMediaPlayer.reset();
            MusicItem music = mMusicList.get(mCurrentPosition);
            if(music != null) {
                music.setCurrentTime(0);
                try {
                    mMediaPlayer.setDataSource(music.getPath());
                    mMediaPlayer.prepare();
                    mCurrentMusic = music;
                } catch (IOException e) {
                    mCurrentMusic = null;
                }
            }
            updateNotification();
        }

        @Override
        public void play() {
            if(mCurrentMusic == null)
                return;
            mMediaPlayer.start();
            isPlaying = true;
            updateNotification();
        }

        @Override
        public void pause() {
            isPlaying = false;
            mMediaPlayer.pause();
            updateNotification();
        }

        @Override
        public void next() {
            updatePosition(true);
            prepare();
            play();
        }

        @Override
        public void prev() {
            updatePosition(false);
            prepare();
            play();
        }

        @Override
        public PlayMode getMode() {
            return mPlayMode;
        }

        @Override
        public void setMode(PlayMode mode) {
            mPlayMode = mode;
        }

        @Override
        public void seekTo(int msec) {
            if(mCurrentMusic == null)
                return;
            mCurrentMusic.setCurrentTime(msec);
            mMediaPlayer.seekTo(msec);
        }

        @Override
        public boolean isPlaying() {
            return isPlaying;
        }
    }

}
