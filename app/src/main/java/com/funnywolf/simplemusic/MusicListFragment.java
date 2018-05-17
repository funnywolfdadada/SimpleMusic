package com.funnywolf.simplemusic;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.funnywolf.simplemusic.Database.MusicItem;
import com.funnywolf.simplemusic.Database.MusicListItem;
import com.funnywolf.simplemusic.Util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicListFragment extends Fragment
    implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener{

    private static final String TAG = "MusicListFragment";

    private TextView mListTitle;
    private ImageView mBackImageView;
    private ListView mMusicListView;

    private MusicListCallback mMusicListCallback;

    private boolean inMusicList = false;

    private ArrayList<MusicListItem> mMusicLists;

    private MusicListItem mCurrentMusicListItem;
    private MusicListItem mPlayingMusicListItem;

    private MusicListItemAdapter mMusicListItemAdapter;
    private MusicItemAdapter mMusicItemAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.music_list_layout, container, false);

        (view.findViewById(R.id.list_title_layout)).setOnClickListener(this);
        mListTitle = view.findViewById(R.id.list_title);
        mBackImageView = view.findViewById(R.id.list_back);

        mMusicListView = view.findViewById(R.id.music_list);
        mMusicListView.setOnItemClickListener(this);
        mMusicListView.setOnTouchListener(this);

        mMusicListCallback = (MusicListCallback) getActivity();

        loadMusicList();

        mMusicListItemAdapter = new MusicListItemAdapter(getActivity(), mMusicLists);
        mMusicItemAdapter = new MusicItemAdapter(getActivity(),
                mPlayingMusicListItem.getMusicList());

        updateFragment();
        return view;
    }

    /**
     * for list_title_layout
     */
    @Override
    public void onClick(View v) {
        if(inMusicList) {
            onBackTouch();
        }else {

        }
    }

    /**
     * for ListView
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(inMusicList) {
            mPlayingMusicListItem.setPlaying(false);
            mPlayingMusicListItem = mCurrentMusicListItem;
            mPlayingMusicListItem.setPlaying(true);
            mMusicListCallback.onMusicItemClick(mPlayingMusicListItem.getMusicList(), position);
        }else {
            inMusicList = true;
            mCurrentMusicListItem = mMusicLists.get(position);
            updateFragment();
        }
    }

    private void updateFragment() {
        if(inMusicList) {
            mMusicItemAdapter.setList(mCurrentMusicListItem.getMusicList());
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

    private void loadMusicList() {
        mMusicLists = new ArrayList<>();

        MusicListItem listItem = new MusicListItem("所有歌曲");
        Utility.getAllMusic(getActivity(), listItem.getMusicList());
        mMusicLists.add(listItem);
        mPlayingMusicListItem = listItem;
        mCurrentMusicListItem = listItem;

        listItem = new MusicListItem("新建歌单1");
        for(int i = 0; i < 10; i++) {
            listItem.getMusicList().add(mPlayingMusicListItem.getMusicList().get(11 + 5 * i));
        }
        mMusicLists.add(listItem);
        listItem = new MusicListItem("新建歌单2");
        for(int i = 0; i < 10; i++) {
            listItem.getMusicList().add(mPlayingMusicListItem.getMusicList().get(22 + 5 * i));
        }
        mMusicLists.add(listItem);

        mPlayingMusicListItem.setPlaying(true);
    }

    private void saveMusicList() {
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
        mMusicListCallback.onMusicListPrepare(mCurrentMusicListItem.getMusicList(), 0);
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
