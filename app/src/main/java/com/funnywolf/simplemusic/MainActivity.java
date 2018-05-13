package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ServiceConnection, MusicListFragment.MusicListCallback {

    private static final String TAG = "SimpleMusic";

    private MusicPanelFragment mMusicPanelFragment;
    private MusicListFragment mMusicListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mMusicPanelFragment = (MusicPanelFragment) fragmentManager.
                findFragmentById(R.id.music_panel);
        mMusicListFragment = (MusicListFragment) fragmentManager.
                findFragmentById(R.id.music_list);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMusicPanelFragment.setMusicController((MusicControl) service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onMusicItemClickListener(List<MusicItem> list, int position) {
        mMusicPanelFragment.play(list, position);
    }

}
