package com.funnywolf.simplemusic;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    private MusicPanelCallback mMusicPanelCallback;
    private UpdatePanelTask updatePanelTask;

    private TextView panelTitle, panelArtist, panelCurrentTime, panelDuration;
    private SeekBar panelSeekBar;
    private Button panelMode, panelPrev, panelPlayPause, panelNext;

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
        panelPlayPause = view.findViewById(R.id.panel_play_pause);
        panelNext = view.findViewById(R.id.panel_next);

        panelSeekBar.setOnSeekBarChangeListener(this);
        panelMode.setOnClickListener(this);
        panelPrev.setOnClickListener(this);
        panelPlayPause.setOnClickListener(this);
        panelNext.setOnClickListener(this);

        mMusicPanelCallback = (MusicPanelCallback) getActivity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePanelTask = new UpdatePanelTask();
        updatePanelTask.execute(mMusicPanelCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        updatePanelTask.cancel(true);
    }

    final String[] PLAY_MODE = {"L", "R", "S"};
    public synchronized void updatePanel(MusicItem music, int position,
                                         MusicControl.PlayMode mode, boolean playing) {
        if (music != null) {
            panelTitle.setText(String.format(Locale.getDefault(),
                    "%d: %s", (position + 1), music.title));
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
        panelMode.setText(PLAY_MODE[mode.ordinal()]);
        if(playing) {
            panelPlayPause.setBackgroundResource(R.drawable.pause);
        }else {
            panelPlayPause.setBackgroundResource(R.drawable.start);
        }
    }

    /**
     * for Buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.panel_mode:
                mMusicPanelCallback.onModeClick();
                break;
            case R.id.panel_prev:
                mMusicPanelCallback.onPrevClick();
                break;
            case R.id.panel_play_pause:
                mMusicPanelCallback.onPlayPauseClick();
                break;
            case R.id.panel_next:
                mMusicPanelCallback.onNextClick();
                break;
            default:
                break;
        }
        mMusicPanelCallback.onPanelUpdate();
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
        mMusicPanelCallback.onSeekTo(seekBar.getProgress());
        mMusicPanelCallback.onPanelUpdate();
    }

    static class UpdatePanelTask extends AsyncTask<MusicPanelCallback, MusicPanelCallback, Boolean> {
        @Override
        protected Boolean doInBackground(MusicPanelCallback... callbacks) {
            while(!isCancelled()) {
                try {
                    publishProgress(callbacks);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(MusicPanelCallback... callbacks) {
            callbacks[0].onPanelUpdate();
        }

    }

    public interface MusicPanelCallback {
        void onPrevClick();
        void onPlayPauseClick();
        void onNextClick();
        void onModeClick();
        void onSeekTo(int progress);
        void onPanelUpdate();
    }

}
