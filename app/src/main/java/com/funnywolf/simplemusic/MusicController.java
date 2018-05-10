package com.funnywolf.simplemusic;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MusicController implements MusicControl, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MusicController";

    private Activity activity;
    private MusicControl musicControlBinder;

    private TextView panelTitle, panelArtist, panelCurrentTime, panelDuration;
    private SeekBar panelSeekBar;
    private Button panelMode, panelPrev, panelStartStop, panelNext;

    private PlayMode playMode;
    private boolean playing = false;
    private UpdateControllerTask updateControllerTask;

    MusicController(Activity activity, MusicControl binder) {
        this.activity = activity;
        musicControlBinder = binder;

        panelTitle = activity.findViewById(R.id.panel_title);
        panelArtist = activity.findViewById(R.id.panel_author);
        panelCurrentTime = activity.findViewById(R.id.panel_current_time);
        panelDuration = activity.findViewById(R.id.panel_duration);

        panelSeekBar = activity.findViewById(R.id.panel_seek_bar);
        panelSeekBar.setOnSeekBarChangeListener(this);

        panelMode = activity.findViewById(R.id.panel_mode);
        panelMode.setOnClickListener(this);
        panelPrev = activity.findViewById(R.id.panel_prev);
        panelPrev.setOnClickListener(this);
        panelStartStop = activity.findViewById(R.id.panel_start_stop);
        panelStartStop.setOnClickListener(this);
        panelNext = activity.findViewById(R.id.panel_next);
        panelNext.setOnClickListener(this);

        updateControllerTask = new UpdateControllerTask();
        updateControllerTask.execute();

        updateController();
    }

    @Override
    public void play(List<MusicItem> list, int position) {
        musicControlBinder.play(list, position);
        start();
        updateController();
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
    public PlayMode getMode() {
        return musicControlBinder.getMode();
    }

    @Override
    public void setMode(PlayMode mode) {
        switch (mode){
            case LIST_LOOP_MODE:
                playMode = PlayMode.LIST_LOOP_MODE;
                panelMode.setText("L");
                break;
            case RANDOM_MODE:
                playMode = PlayMode.RANDOM_MODE;
                panelMode.setText("R");
                break;
            case SINGLE_LOOP_MODE:
                playMode = PlayMode.SINGLE_LOOP_MODE;
                panelMode.setText("S");
                break;
            default:
                break;
        }
        playMode = mode;
        musicControlBinder.setMode(mode);
    }

    @Override
    public void seekTo(int msec) {
        musicControlBinder.seekTo(msec);
    }

    @Override
    public MusicItem getCurrentMusic() {
        return musicControlBinder.getCurrentMusic();
    }

    /**
     * for Buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.panel_mode:
                setMode(playMode);
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
        pause();
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekTo(seekBar.getProgress());
        start();
    }

    public void updateController() {
        MusicItem music = getCurrentMusic();
        if(music != null) {
            panelTitle.setText(music.title);
            panelArtist.setText(music.artist);
            panelDuration.setText(music.duration);
            int currentTime = music.currentTime / 1000;
            panelCurrentTime.setText(String.format(Locale.getDefault(),
                    "%d:%02d", currentTime / 60, currentTime % 60));
            panelSeekBar.setProgress(100 * music.currentTime / music.durationInt);
        }else {
            panelTitle.setText("---");
            panelArtist.setText("---");
            panelDuration.setText("00:00");
            panelCurrentTime.setText("00:00");
            panelSeekBar.setProgress(0);
        }
        setMode(getMode());
    }

    class UpdateControllerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(playing) {
                        publishProgress();
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            updateController();
        }
    }
}
