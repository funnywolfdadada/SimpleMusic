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
        implements ServiceConnection, MusicListFragment.MusicListCallback,
        MusicPanelFragment.MusicPanelCallback {

    private static final String TAG = "SimpleMusic";

    private FragmentManager mFragmentManager;
    private MusicPanelFragment mMusicPanelFragment;
    private MusicListFragment mMusicListFragment;

    private MusicControl mMusicController;

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

    /**
     * for ServiceConnection
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMusicController = ((MusicControl) service);
        mMusicListFragment.musicServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * for MusicListFragment.MusicListCallback
     */

    @Override
    public void onMusicListChange(List<MusicItem> list, int position) {
        if (mMusicController == null)
            return;
        mMusicController.setMusicList(list);
        mMusicController.setCurrentPosition(position);
    }

    @Override
    public void onMusicListPrepare(List<MusicItem> list, int position) {
        if (mMusicController == null)
            return;
        mMusicController.setMusicList(list);
        mMusicController.setCurrentPosition(position);
        mMusicController.prepare();
        onPanelUpdate();
    }

    @Override
    public void onMusicItemClick(List<MusicItem> list, int position) {
        if (mMusicController == null)
            return;
        mMusicController.setMusicList(list);
        mMusicController.setCurrentPosition(position);
        mMusicController.prepare();
        mMusicController.play();
        onPanelUpdate();
    }

    @Override
    public void onSlideDirectionChange(boolean slideUp) {
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
    public void onChangeBackground(boolean update) {
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

    /**
     * for MusicPanelFragment.MusicPanelCallback
     */
    @Override
    public void onPrevClick() {
        if (mMusicController == null)
            return;
        mMusicController.prev();
    }

    @Override
    public void onPlayPauseClick() {
        if (mMusicController == null)
            return;
        if (mMusicController.isPlaying()) {
            mMusicController.pause();
        }else {
            mMusicController.play();
        }
    }

    @Override
    public void onNextClick() {
        if (mMusicController == null)
            return;
        mMusicController.next();
    }

    @Override
    public void onModeClick() {
        if (mMusicController == null)
            return;
        switch (mMusicController.getMode()){
            case LIST_LOOP_MODE:
                mMusicController.setMode(MusicControl.PlayMode.RANDOM_MODE);
                break;
            case RANDOM_MODE:
                mMusicController.setMode(MusicControl.PlayMode.SINGLE_LOOP_MODE);
                break;
            case SINGLE_LOOP_MODE:
                mMusicController.setMode(MusicControl.PlayMode.LIST_LOOP_MODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSeekTo(int progress) {
        if (mMusicController == null)
            return;
        mMusicController.seekTo(progress);
    }

    @Override
    public void onPanelUpdate() {
        if (mMusicController == null)
            return;
        mMusicPanelFragment.updatePanel(mMusicController.getCurrentMusic(),
                mMusicController.getCurrentPosition(), mMusicController.getMode(),
                mMusicController.isPlaying());
    }
}
