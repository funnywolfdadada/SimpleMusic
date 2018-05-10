package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ServiceConnection{

    private static final String TAG = "MainActivity";

    private MusicController musicController;

    private TextView mListTitle;
    private ListView mMusicListView;
    private MusicItemAdapter mMusicItemAdapter;
    private List<MusicItem> mMusicList = new ArrayList<MusicItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);

        mMusicListView = findViewById(R.id.music_list);
        mMusicList = getAllMusic();
        mMusicItemAdapter = new MusicItemAdapter(this, mMusicList);
        mMusicListView.setAdapter(mMusicItemAdapter);
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(musicController != null) {
                    musicController.play(mMusicList, position);
                }
            }
        });
        mListTitle = findViewById(R.id.list_title);
        mListTitle.setText("Total: " + mMusicList.size());
    }

    private ArrayList<MusicItem> getAllMusic() {
        ArrayList<MusicItem> list = new ArrayList<MusicItem>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor != null && cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Long size = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                list.add(new MusicItem(name, title, artist, path, duration, size));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicController = new MusicController(MainActivity.this, (MusicControl)service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


}
