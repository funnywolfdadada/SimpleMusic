package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ServiceConnection, MusicListFragment.MusicListCallback {

    private static final String TAG = "SimpleMusic";

    private FragmentManager mFragmentManager;
    private MusicPanelFragment mMusicPanelFragment;
    private MusicListFragment mMusicListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        mMusicPanelFragment = (MusicPanelFragment) mFragmentManager.
                findFragmentById(R.id.music_panel);
        mMusicListFragment = (MusicListFragment) mFragmentManager.
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
        mMusicPanelFragment.playAndStart(list, position);
    }

    @Override
    public void onSlideListener(boolean slideUp) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(slideUp) {
            fragmentTransaction.hide(mMusicPanelFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        }else {
            fragmentTransaction.show(mMusicPanelFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        fragmentTransaction.commit();
    }

}
