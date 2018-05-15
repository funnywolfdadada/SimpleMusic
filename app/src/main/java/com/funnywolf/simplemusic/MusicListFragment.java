package com.funnywolf.simplemusic;

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
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MusicListFragment extends Fragment
    implements AdapterView.OnItemClickListener, View.OnTouchListener{

    private static final String TAG = "MusicListFragment";

    private Toolbar mToolbar;
    private TextView mListTitle;
    private ListView mMusicListView;

    private boolean inMusicList = false;
    private MusicListItemAdapter mMusicListItemAdapter;
    private ArrayList<MusicListItem> mMusicLists;
    private MusicListItem mCurrentMusicListItem;
    private MusicItemAdapter mMusicItemAdapter;
    private ArrayList<MusicItem> mCurrentMusicList;

    private MusicListCallback mMusicListCallback;

    private static Gson mGson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_list_layout, container, false);

        mToolbar = view.findViewById(R.id.toolbar);
        mMusicListView = view.findViewById(R.id.music_list);
        mListTitle = view.findViewById(R.id.list_title);

        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);

        mMusicListCallback = (MusicListCallback) getActivity();

        initMusicList();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list:
                Log.d(TAG, "onOptionsItemSelected: add_list");
                break;
            case R.id.setting_background:
                mMusicListCallback.onChangeBackground(false);
                break;
            case R.id.update_background:
                mMusicListCallback.onChangeBackground(true);
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(inMusicList) {
            mMusicListCallback.onMusicItemClick(mCurrentMusicList, position);
        }else {
            inMusicList = true;
            mCurrentMusicListItem = mMusicLists.get(position);
            mCurrentMusicList = mCurrentMusicListItem.getMusicList();
            mMusicItemAdapter.setList(mCurrentMusicList);
            mMusicListView.setAdapter(mMusicItemAdapter);
            mListTitle.setText(String.format(Locale.getDefault(), "%s: 共 %d 首",
                    mCurrentMusicListItem.getName(), mCurrentMusicListItem.getCapacity()));
        }
    }

    public boolean onBackTouch() {
        if(inMusicList) {
            inMusicList = false;
            mMusicListItemAdapter.setList(mMusicLists);
            mMusicListView.setAdapter(mMusicListItemAdapter);
            mListTitle.setText("歌单: " + mMusicLists.size());
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
        getAllMusic(mCurrentMusicListItem);
        mMusicLists.add(mCurrentMusicListItem);

        mCurrentMusicList = mCurrentMusicListItem.getMusicList();

        mMusicListItemAdapter = new MusicListItemAdapter(getActivity(), mMusicLists);

        mMusicItemAdapter = new MusicItemAdapter(getActivity(), mCurrentMusicList);

        mMusicListView.setAdapter(mMusicListItemAdapter);
        mMusicListView.setOnItemClickListener(this);
        mMusicListView.setOnTouchListener(this);
        mListTitle.setText("歌单: " + mMusicLists.size());

    }

    private void loadMusicList() {

    }

    private void saveMusicList() {

    }

    private void getAllMusic(MusicListItem listItem) {
        ArrayList<MusicItem> list = listItem.getMusicList();
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
                list.add(new MusicItem(listItem, name, title, artist, path, duration, size));
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
                    mToolbar.setVisibility(View.VISIBLE);
                }else if(lastY < mFirstY && !mLastDirection) {
                    mLastDirection = true;
                    mMusicListCallback.onSlideDirectionChange(true);
                    mToolbar.setVisibility(View.GONE);
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
        void onChangeBackground(boolean update);
    }
}
