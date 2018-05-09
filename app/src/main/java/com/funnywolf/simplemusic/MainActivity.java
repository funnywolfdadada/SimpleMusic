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

    private static final int FILE_SELECT_CODE = 0;
    private MusicController musicController;

    private ListView listView;
    private List<String> mArrayList = new ArrayList<String>();

    private ListView mMusicListView;
    private MusicItemAdapter musicItemAdapter;
    private List<MusicItem> mMusicList = new ArrayList<MusicItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);

        Button selectFile = findViewById(R.id.select_file);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        /*
        listView = findViewById(R.id.music_list);
        for(int i = 0; i < 100; i++) {
            mArrayList.add("第" + i + "个View");
        }
        ListViewAdapter adapter = new ListViewAdapter(this, mArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, mArrayList.get(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
        */
        mMusicListView = findViewById(R.id.music_list);
        mMusicList = getAllMusic();
        for(MusicItem m : mMusicList){
            Log.d(TAG, "onCreate: " + m);
        }
        musicItemAdapter = new MusicItemAdapter(this, mMusicList);
        mMusicListView.setAdapter(musicItemAdapter);
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, mMusicList.get(position).toString(),
                //        Toast.LENGTH_SHORT).show();
                if(musicController != null) {
                    musicController.play(mMusicList.get(position).getPath());
                }
            }
        });
    }

    private ArrayList<MusicItem> getAllMusic() {
        ArrayList<MusicItem> list = new ArrayList<MusicItem>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor.moveToFirst()){
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
        }
        cursor.close();
        return list;
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        musicController = new MusicController((MusicControl)service);
        //musicController.play(Environment.getExternalStorageDirectory().getPath() + "music.mp3");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private class MusicController implements MusicControl, View.OnClickListener,
            SeekBar.OnSeekBarChangeListener {
        private MusicControl musicControlBinder;

        private TextView panelTitle, panelAuthor, panelCurrentTime, panelTotalTime;
        private SeekBar panelSeekBar;
        private Button panelMode, panelPrev, panelStartStop, panelNext;

        private PlayMode playMode = PlayMode.LIST_LOOP_MODE;
        private boolean playing = false;

        MusicController(MusicControl binder) {
            musicControlBinder = binder;

            panelTitle = findViewById(R.id.panel_title);
            panelAuthor = findViewById(R.id.panel_author);
            panelCurrentTime = findViewById(R.id.panel_current_time);
            panelTotalTime = findViewById(R.id.panel_total_time);

            panelSeekBar = findViewById(R.id.panel_seek_bar);
            panelSeekBar.setOnSeekBarChangeListener(this);

            panelMode = findViewById(R.id.panel_mode);
            panelMode.setOnClickListener(this);
            panelPrev = findViewById(R.id.panel_prev);
            panelPrev.setOnClickListener(this);
            panelStartStop = findViewById(R.id.panel_start_stop);
            panelStartStop.setOnClickListener(this);
            panelNext = findViewById(R.id.panel_next);
            panelNext.setOnClickListener(this);
        }

        @Override
        public boolean play(String path) {
            boolean playOk;
            musicController.stop();
            playOk = musicControlBinder.play(path);
            musicController.start();
            return playOk;
        }

        @Override
        public void stop() {
            pause();
            musicControlBinder.stop();
        }

        @Override
        public void start() {
            playing = true;
            panelStartStop.setText("PAUSE");
            musicControlBinder.start();
        }

        @Override
        public void pause() {
            playing = false;
            panelStartStop.setText("START");
            musicControlBinder.pause();
        }

        @Override
        public void next() {
            musicControlBinder.next();
        }

        @Override
        public void prev() {
            musicControlBinder.prev();
        }

        @Override
        public void changeMode(PlayMode mode) {
            switch (playMode){
                case LIST_LOOP_MODE:
                    playMode = PlayMode.RANDOM_MODE;
                    panelMode.setText("R");
                    break;
                case RANDOM_MODE:
                    playMode = PlayMode.SINGLE_LOOP_MODE;
                    panelMode.setText("S");
                    break;
                case SINGLE_LOOP_MODE:
                    playMode = PlayMode.LIST_LOOP_MODE;
                    panelMode.setText("L");
                    break;
                default:
                    break;
            }
            musicControlBinder.changeMode(mode);
        }

        @Override
        public int getCurrentPosition() {
            return musicControlBinder.getCurrentPosition();
        }

        @Override
        public int getDuration() {
            return musicControlBinder.getDuration();
        }

        @Override
        public void seekTo(int msec) {
            musicControlBinder.seekTo(msec);
        }

        @Override
        public MusicItem getCurrentMusic() {
            return null;
        }

        /**
         * for Buttons
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
            case R.id.panel_mode:
                changeMode(playMode);
                break;
            case R.id.panel_prev:
                prev();
                break;
            case R.id.panel_start_stop:
                if (playing) {
                    pause();
                }else {
                    start();
                }
                break;
            case R.id.panel_next:
                next();
                break;
            default:
                break;
            }
        }

        /**
         * for SeekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            setProgress(seekBar.getProgress());
        }
    }
}
