package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ServiceConnection{

    private static final int FILE_SELECT_CODE = 0;
    private MusicController musicController;
    private MusicControl musicControlBinder;

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
        musicControlBinder = (MusicControl)service;
        musicController = new MusicController(musicControlBinder);
        musicController.play(Environment.getExternalStorageDirectory().getPath() + "/music.mp3");
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
            return musicControlBinder.play(path);
        }

        @Override
        public void start() {
            musicControlBinder.start();
        }

        @Override
        public void pause() {
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
                changeMode(playMode);
                break;
            case R.id.panel_prev:
                prev();
                break;
            case R.id.panel_start_stop:
                if (playing) {
                    playing = false;
                    pause();
                    panelStartStop.setText("START");
                }else {
                    playing = true;
                    start();
                    panelStartStop.setText("PAUSE");
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
