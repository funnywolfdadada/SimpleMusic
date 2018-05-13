package com.funnywolf.simplemusic;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class MusicPanelFragment extends Fragment
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "MusicPanelFragment";

    private MusicControl mMusicController;
    private UpdatePanelTask updatePanelTask;

    private TextView panelTitle, panelArtist, panelCurrentTime, panelDuration;
    private SeekBar panelSeekBar;
    private Button panelMode, panelPrev, panelStartStop, panelNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_panel_layout, container, false);
        panelTitle = view.findViewById(R.id.panel_title);
        panelArtist = view.findViewById(R.id.panel_author);
        panelCurrentTime = view.findViewById(R.id.panel_current_time);
        panelDuration = view.findViewById(R.id.panel_duration);
        panelSeekBar = view.findViewById(R.id.panel_seek_bar);
        panelMode = view.findViewById(R.id.panel_mode);
        panelPrev = view.findViewById(R.id.panel_prev);
        panelStartStop = view.findViewById(R.id.panel_start_stop);
        panelNext = view.findViewById(R.id.panel_next);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePanelTask = new UpdatePanelTask();
        updatePanelTask.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        updatePanelTask.cancel(true);
    }

    public void setMusicController(MusicControl controller) {
        mMusicController = controller;
        panelSeekBar.setOnSeekBarChangeListener(this);
        panelMode.setOnClickListener(this);
        panelPrev.setOnClickListener(this);
        panelStartStop.setOnClickListener(this);
        panelNext.setOnClickListener(this);
        updatePanel();
    }

    public void play(List<MusicItem> list, int position) {
        mMusicController.play(list, position);
        mMusicController.start();
        updatePanel();
    }

    public synchronized void updatePanel() {
        if(mMusicController == null) {
            return;
        }
        MusicItem music = mMusicController.getCurrentMusic();
        if (music != null) {
            panelTitle.setText(music.title);
            panelArtist.setText(music.artist);
            panelDuration.setText(music.duration);
            int currentTime = music.currentTime / 1000;
            panelCurrentTime.setText(String.format(Locale.getDefault(),
                    "%d:%02d", currentTime / 60, currentTime % 60));
            panelSeekBar.setProgress(100 * music.currentTime / music.durationInt);
        } else {
            panelTitle.setText("------");
            panelArtist.setText("------");
            panelDuration.setText("00:00");
            panelCurrentTime.setText("00:00");
            panelSeekBar.setProgress(0);
        }
        switch(mMusicController.getMode()) {
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
        if(mMusicController.isPlaying()) {
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
                break;
            case R.id.panel_prev:
                mMusicController.prev();
                break;
            case R.id.panel_start_stop:
                if (mMusicController.isPlaying()) {
                    mMusicController.pause();
                }else {
                    mMusicController.start();
                }
                break;
            case R.id.panel_next:
                mMusicController.next();
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
        mMusicController.pause();
        updatePanel();
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMusicController.seekTo(seekBar.getProgress());
        mMusicController.start();
        updatePanel();
    }

    class UpdatePanelTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            while(!isCancelled()) {
                try {
                    publishProgress();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            updatePanel();
        }
    }


}
