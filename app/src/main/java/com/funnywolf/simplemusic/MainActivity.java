package com.funnywolf.simplemusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity
        implements ServiceConnection, MusicListFragment.MusicListCallback {

    private static final String TAG = "Music";

    private MusicPanel musicPanel;
    private UpdatePanelTask updatePanelTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(musicPanel == null)
            musicPanel = new MusicPanel(MainActivity.this, (MusicControl)service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePanelTask = new UpdatePanelTask();
        updatePanelTask.execute();
    }

    @Override
    protected void onStop() {
        updatePanelTask.cancel(true);
        super.onStop();
    }

    @Override
    public void onMusicItemClickListener(List<MusicItem> list, int position) {
        musicPanel.play(list, position);
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
            if(musicPanel != null)
                musicPanel.updatePanel();
        }
    }

}
