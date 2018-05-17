package com.funnywolf.simplemusic;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MusicListFragment extends Fragment
    implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener{

    private static final String TAG = "MusicListFragment";

    private Toolbar mToolbar;
    private TextView mListTitle;
    private ImageView mBackImageView;
    private ListView mMusicListView;

    private boolean inMusicList = false;
    private ArrayList<MusicListItem> mMusicLists;
    private MusicListItemAdapter mMusicListItemAdapter;
    private MusicListItem mCurrentMusicListItem;
    private ArrayList<MusicItem> mCurrentMusicList;
    private MusicItemAdapter mMusicItemAdapter;

    private MusicListCallback mMusicListCallback;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_list_layout, container, false);

        mToolbar = view.findViewById(R.id.toolbar);

        (view.findViewById(R.id.list_title_layout)).setOnClickListener(this);
        mListTitle = view.findViewById(R.id.list_title);
        mBackImageView = view.findViewById(R.id.list_back);

        mMusicListView = view.findViewById(R.id.music_list);
        mMusicListView.setOnItemClickListener(this);
        mMusicListView.setOnTouchListener(this);

        mMusicListCallback = (MusicListCallback) getActivity();

        initMusicList();
        updateFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        if(inMusicList) {
            onBackTouch();
        }else {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(inMusicList) {
            mMusicListCallback.onMusicItemClick(mCurrentMusicList, position);
        }else {
            inMusicList = true;
            mCurrentMusicListItem = mMusicLists.get(position);
            mCurrentMusicList = mCurrentMusicListItem.getMusicList();
            updateFragment();
        }
    }

    private void updateFragment() {
        if(inMusicList) {
            mMusicItemAdapter.setList(mCurrentMusicList);
            mMusicListView.setAdapter(mMusicItemAdapter);
            mListTitle.setText(String.format(Locale.getDefault(), "%s: 共 %d 首",
                    mCurrentMusicListItem.getName(), mCurrentMusicListItem.getCapacity()));
            mBackImageView.setBackgroundResource(R.drawable.ic_back);
        }else {
            mMusicListItemAdapter.setList(mMusicLists);
            mMusicListView.setAdapter(mMusicListItemAdapter);
            mListTitle.setText("歌单: " + mMusicLists.size());
            mBackImageView.setBackgroundResource(R.drawable.ic_add_list);
        }
    }

    public boolean onBackTouch() {
        if(inMusicList) {
            inMusicList = false;
            updateFragment();
            return false;
        }
        return true;
    }

    public void musicServiceConnected() {
        mMusicListCallback.onMusicListPrepare(mCurrentMusicList, 0);
    }

    private void initMusicList() {
        mMusicLists = new ArrayList<>();

        mCurrentMusicListItem = new MusicListItem("所有歌曲");
        mCurrentMusicList = mCurrentMusicListItem.getMusicList();
        getAllMusic(mCurrentMusicList);

        mMusicLists.add(mCurrentMusicListItem);
        mMusicLists.add(new MusicListItem("新建歌单1"));
        mMusicLists.add(new MusicListItem("新建歌单2"));

        mMusicListItemAdapter = new MusicListItemAdapter(getActivity(), mMusicLists);
        mMusicItemAdapter = new MusicItemAdapter(getActivity(), mCurrentMusicList);
    }

    public void loadMusicList() {

    }

    public void saveMusicList() {
    }

    private void getAllMusic(ArrayList<MusicItem> list) {
        Cursor cursor = getActivity().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor != null && cursor.moveToFirst()) {
            while(!cursor.isAfterLast()){
                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
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
                list.add(new MusicItem(id, name, title, artist, path, duration, size));
                cursor.moveToNext();
            }
            cursor.close();
        }
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
                    mMusicListCallback.onSlideDirectionChange(false);
                }else if(lastY < mFirstY && !mLastDirection) {
                    mLastDirection = true;
                    mMusicListCallback.onSlideDirectionChange(true);
                }
                break;
        }

        return false;
    }

    public interface MusicListCallback {
        void onMusicListChange(List<MusicItem> list, int position);
        void onMusicListPrepare(List<MusicItem> list, int position);
        void onMusicItemClick(List<MusicItem> list, int position);
        void onSlideDirectionChange(boolean slideUp);
    }
}
