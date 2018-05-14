package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ServiceConnection, MusicListFragment.MusicListCallback {

    private static final String TAG = "SimpleMusic";

    private FragmentManager mFragmentManager;
    private MusicPanelFragment mMusicPanelFragment;
    private MusicListFragment mMusicListFragment;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.background);

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

    private static final int SELECT_PICTURE_CODE = 111;
    @Override
    public void onChangeBackgroundListener(boolean update) {
        if(update) {
            Utility.loadBingPicture(this, mImageView);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PICTURE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null)
            return;
        switch (requestCode) {
            case SELECT_PICTURE_CODE:
                RequestOptions options = new RequestOptions().centerCrop();
                Glide.with(this).load(data.getData()).apply(options).into(mImageView);
                break;
            default:
                break;
        }
    }

}
