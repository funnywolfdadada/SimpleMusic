package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.funnywolf.simplemusic.Util.Utility;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements Handler.Callback, ServiceConnection, MusicListFragment.MusicListCallback,
        MusicPanelFragment.MusicPanelCallback {

    private static final String TAG = "SimpleMusic";

    public static final int MSG_BACKGROUND = 1;

    private Toolbar mToolbar;
    private ImageView mImageView;

    private Handler mHandler;

    private FragmentManager mFragmentManager;
    private MusicPanelFragment mMusicPanelFragment;
    private MusicListFragment mMusicListFragment;

    private static MusicControl mMusicController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.background);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mHandler = new Handler(this);

        mFragmentManager = getSupportFragmentManager();
        mMusicPanelFragment = (MusicPanelFragment) mFragmentManager.
                findFragmentById(R.id.music_panel);
        mMusicListFragment = (MusicListFragment) mFragmentManager.
                findFragmentById(R.id.music_list);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);

        SharedPreferences preferences = getSharedPreferences("SimpleMusic", MODE_PRIVATE);
        String background = preferences.getString("background", null);
        if(background != null) {
            if(background.charAt(0) == '/') {
                mHandler.sendMessage(Message.obtain(mHandler, MSG_BACKGROUND, background));
            }else {
                mHandler.sendMessage(Message.obtain(mHandler, MSG_BACKGROUND,
                        Uri.parse(background)));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mMusicListFragment.onBackTouch())
            moveTaskToBack(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_background:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, MSG_BACKGROUND);
                break;
            case R.id.update_background:
                Utility.loadBingPicture(mHandler);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null)
            return;
        switch (requestCode) {
            case MSG_BACKGROUND:
                mHandler.sendMessage(Message.obtain(mHandler, MSG_BACKGROUND,
                        data.getData()));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(isFinishing())
            return false;
        switch (msg.what) {
            case MSG_BACKGROUND:
                if(msg.obj == null) {
                    break;
                }
                if("".equals(msg.obj.toString())) {
                    Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(msg.obj.toString().charAt(0) == '/') {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(msg.obj.toString()));
                    intent.setData(uri);
                    sendBroadcast(intent);
                }
                SharedPreferences.Editor editor = getSharedPreferences("SimpleMusic",
                        MODE_PRIVATE).edit();
                editor.putString("background", msg.obj.toString());
                editor.apply();
                RequestOptions options = new RequestOptions().centerCrop();
                Glide.with(this).load(msg.obj).apply(options).into(mImageView);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * for ServiceConnection
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(mMusicController == null) {
            mMusicController = ((MusicControl) service);
            mMusicListFragment.musicServiceConnected();
        }
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
            mToolbar.setVisibility(View.GONE);
            fragmentTransaction.hide(mMusicPanelFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        }else {
            mToolbar.setVisibility(View.VISIBLE);
            fragmentTransaction.show(mMusicPanelFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        fragmentTransaction.commit();
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
