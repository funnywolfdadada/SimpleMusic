package com.funnywolf.simplemusic;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MusicPanel implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MusicPanel";

    private MusicControl musicController;

    private TextView panelTitle, panelArtist, panelCurrentTime, panelDuration;
    private SeekBar panelSeekBar;
    private Button panelMode, panelPrev, panelStartStop, panelNext;

    private MusicPanel(Activity activity, MusicControl controller) {
        musicController = controller;

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

        updatePanel();
    }

    private static MusicPanel mMusicPanel = null;
    public static MusicPanel getInstance(Activity activity, MusicControl controller) {
        if(mMusicPanel == null) {
            mMusicPanel = new MusicPanel(activity, controller);
        }
        return mMusicPanel;
    }

    public void play(List<MusicItem> list, int position) {
        musicController.play(list, position);
        updatePanel();
    }

    public synchronized void updatePanel() {
        MusicItem music = musicController.getCurrentMusic();

        if (music != null) {
            panelTitle.setText(music.title);
            panelArtist.setText(music.artist);
            panelDuration.setText(music.duration);
            int currentTime = music.currentTime / 1000;
            panelCurrentTime.setText(String.format(Locale.getDefault(),
                    "%d:%02d", currentTime / 60, currentTime % 60));
            panelSeekBar.setProgress(100 * music.currentTime / music.durationInt);
        } else {
            panelTitle.setText("---");
            panelArtist.setText("---");
            panelDuration.setText("00:00");
            panelCurrentTime.setText("00:00");
            panelSeekBar.setProgress(0);
        }
        switch(musicController.getMode()) {
            case LIST_LOOP_MODE:
                panelMode.setText("L");
                break;
            case RANDOM_MODE:
                panelMode.setText("R");
                break;
            case SINGLE_LOOP_MODE:
                panelMode.setText("S");
                break;
        }
        if(musicController.isPlaying()) {
            panelStartStop.setBackgroundResource(R.drawable.pause);
        }else {
            panelStartStop.setBackgroundResource(R.drawable.start);
        }
    }

    /**
     * for Buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.panel_mode:
                switch (musicController.getMode()){
                    case LIST_LOOP_MODE:
                        musicController.setMode(MusicControl.PlayMode.RANDOM_MODE);
                        break;
                    case RANDOM_MODE:
                        musicController.setMode(MusicControl.PlayMode.SINGLE_LOOP_MODE);
                        break;
                    case SINGLE_LOOP_MODE:
                        musicController.setMode(MusicControl.PlayMode.LIST_LOOP_MODE);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.panel_prev:
                musicController.prev();
                break;
            case R.id.panel_start_stop:
                if (musicController.isPlaying()) {
                    musicController.pause();
                }else {
                    musicController.start();
                }
                break;
            case R.id.panel_next:
                musicController.next();
                break;
            default:
                break;
        }
        updatePanel();
    }

    /**
     * for SeekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        musicController.pause();
        updatePanel();
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        musicController.seekTo(seekBar.getProgress());
        musicController.start();
        updatePanel();
    }

}
