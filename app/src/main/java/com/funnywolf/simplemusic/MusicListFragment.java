package com.funnywolf.simplemusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment
    implements AdapterView.OnItemClickListener, View.OnTouchListener{

    private static final String TAG = "MusicListFragment";
    
    private TextView mListTitle;
    private ListView mMusicListView;
    private MusicItemAdapter mMusicItemAdapter;
    private List<MusicItem> mMusicList = new ArrayList<>();
    private MusicListCallback mMusicListCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list_layout, container, false);

        mMusicListView = view.findViewById(R.id.music_list);
        mListTitle = view.findViewById(R.id.list_title);

        mMusicListCallback = (MusicListCallback) getActivity();

        mMusicList = getAllMusic();
        mMusicItemAdapter = new MusicItemAdapter(getActivity(), mMusicList);
        mMusicListView.setAdapter(mMusicItemAdapter);
        mMusicListView.setOnItemClickListener(this);
        mMusicListView.setOnTouchListener(this);
        mListTitle.setText("Total: " + mMusicList.size());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private ArrayList<MusicItem> getAllMusic() {
        ArrayList<MusicItem> list = new ArrayList<MusicItem>();
        Cursor cursor = getActivity().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor != null && cursor.moveToFirst()) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mMusicListCallback.onMusicItemClickListener(mMusicList, position);
    }

    /**
     * last slide direction
     * true: slide up, false: slider down
     */
    private boolean mLastDirection = false;
    private float mFirstY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float lastY = event.getY();
                if(lastY > mFirstY && mLastDirection) {
                    mLastDirection = false;
                    mMusicListCallback.onSlideListener(false);
                    mListTitle.setVisibility(View.VISIBLE);
                }else if(lastY < mFirstY && !mLastDirection) {
                    mLastDirection = true;
                    mMusicListCallback.onSlideListener(true);
                    mListTitle.setVisibility(View.GONE);
                }
                break;
        }

        return false;
    }

    public interface MusicListCallback {
        void onMusicItemClickListener(List<MusicItem> list, int position);
        void onSlideListener(boolean slideUp);
    }
}
