package com.funnywolf.simplemusic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.funnywolf.simplemusic.Database.MusicItem;
import com.funnywolf.simplemusic.Database.MusicList;
import com.funnywolf.simplemusic.Util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MusicListFragment extends Fragment
    implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, View.OnTouchListener{

    private static final String TAG = "MusicListFragment";

    private TextView mListTitle;
    private ImageView mBackImageView;
    private ListView mMusicListView;

    private MusicListCallback mMusicListCallback;

    static private boolean inMusicList = false;
    static private MusicList<MusicList<MusicItem>> mMusicLists;
    static private MusicList<MusicItem> mCurrentMusicList;
    static private MusicList<MusicItem> mPlayingMusicList;

    private MusicListAdapter mMusicListAdapter;
    private MusicItemAdapter mMusicItemAdapter;

    private EditText dialogEditText;
    private AlertDialog addMusicListDialog;
    private EditText renameEditText;
    private AlertDialog musicListLongClickDialog;
    private AlertDialog musicItemLongClickDialog;
    private AlertDialog.Builder addMusicItemDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicListCallback = (MusicListCallback) getActivity();

        if(mMusicLists == null)
            loadMusicList();

        mMusicListAdapter = new MusicListAdapter(getActivity(), mMusicLists);
        mMusicItemAdapter = new MusicItemAdapter(getActivity(), mPlayingMusicList);

        dialogEditText = new EditText(getActivity());
        addMusicListDialog = new AlertDialog.Builder(getActivity())
                .setTitle("新建歌单")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = dialogEditText.getText().toString();
                        if("".equals(name))
                            name = dialogEditText.getHint().toString();
                        if(mMusicLists.contains(name)) {
                            Toast.makeText(getActivity(), name + "已经存在！", Toast.LENGTH_SHORT).show();
                        }else {
                            mMusicLists.add(new MusicList<MusicItem>(name));
                            updateFragment();
                        }
                    }
                })
                .setView(dialogEditText)
                .create();
        renameEditText = new EditText(getActivity());
        musicListLongClickDialog = new AlertDialog.Builder(getActivity())
                .setTitle("歌单")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = renameEditText.getText().toString();
                        if(mLongClickMusicList.getName().equals(name))
                            return;
                        if("所有歌曲".equals(mLongClickMusicList.getName())) {
                            Toast.makeText(getActivity(), "无法重命名该歌单！", Toast.LENGTH_SHORT).show();
                        }else {
                            if(mMusicLists.rename(mMusicLists.indexOf(mLongClickMusicList), name)) {
                                mLongClickMusicList.setName(name);
                            }else {
                                Toast.makeText(getActivity(), "该名字已存在", Toast.LENGTH_SHORT).show();
                            }
                            updateFragment();
                        }
                    }
                })
                .setNeutralButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if("所有歌曲".equals(mLongClickMusicList.getName())) {
                            Toast.makeText(getActivity(), "无法删除该歌单！", Toast.LENGTH_SHORT).show();
                        }else {
                            mMusicLists.remove(mLongClickMusicList);
                            updateFragment();
                        }
                    }
                })
                .setView(renameEditText)
                .create();

        musicItemLongClickDialog = new AlertDialog.Builder(getActivity())
                .setItems(new String[]{"删除", "添加到", "详细信息"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if("所有歌曲".equals(mCurrentMusicList.getName())) {
                                            Toast.makeText(getActivity(), "无法删除该歌曲！",
                                                    Toast.LENGTH_SHORT).show();
                                        }else {
                                            mCurrentMusicList.remove(mLongClickMusicItem);
                                            updateFragment();
                                        }
                                        break;
                                    case 1:
                                        addMusicItemDialog.setItems(mMusicLists.getAllItemsName(),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(which == 0)
                                                            return;
                                                        mMusicLists.get(which).add(mLongClickMusicItem);
                                                    }
                                                });
                                        addMusicItemDialog.show();
                                        break;
                                    case 2:
                                        Toast.makeText(getActivity(),
                                                String.format(Locale.getDefault(),
                                                "文件名: %s\n歌名: %s\n歌手: %s\n大小: %s\n" +
                                                        "时长: %s\n路径%s",
                                                        mLongClickMusicItem.getName(),
                                                        mLongClickMusicItem.getTitle(),
                                                        mLongClickMusicItem.getArtist(),
                                                        mLongClickMusicItem.getSize(),
                                                        mLongClickMusicItem.getDuration(),
                                                        mLongClickMusicItem.getPath()
                                            ), Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                .create();

        addMusicItemDialog = new AlertDialog.Builder(getActivity())
                .setTitle("添加到");
    }

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
        mMusicListView.setOnItemLongClickListener(this);
        mMusicListView.setOnTouchListener(this);

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
            dialogEditText.setText("");
            dialogEditText.setHint("新建歌单" + mMusicLists.size());
            addMusicListDialog.show();
        }
    }

    /**
     * for ListView
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(inMusicList) {
            mPlayingMusicList.setPlaying(false);
            mPlayingMusicList = mCurrentMusicList;
            mPlayingMusicList.setPlaying(true);
            mMusicListCallback.onMusicItemClick(mPlayingMusicList, position);
        }else {
            inMusicList = true;
            mCurrentMusicList = mMusicLists.get(position);
            updateFragment();
        }
    }

    MusicList<MusicItem> mLongClickMusicList;
    MusicItem mLongClickMusicItem;
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(inMusicList) {
            mLongClickMusicItem = mCurrentMusicList.get(position);
            musicItemLongClickDialog.setTitle("歌曲： " + mLongClickMusicItem.getTitle());
            musicItemLongClickDialog.show();
        }else {
            mLongClickMusicList = mMusicLists.get(position);
            renameEditText.setText(mLongClickMusicList.getName());
            musicListLongClickDialog.show();
        }
        return true;
    }

    private void loadMusicList() {
        mMusicLists = new MusicList<>("SimpleMusic");

        MusicList<MusicItem> listItem = new MusicList<>("所有歌曲");
        Utility.getAllMusic(getActivity(), listItem);
        mMusicLists.add(listItem);
        mPlayingMusicList = listItem;
        mCurrentMusicList = listItem;

        mPlayingMusicList.setPlaying(true);
    }

    private void updateFragment() {
        if(inMusicList) {
            mMusicItemAdapter.setList(mCurrentMusicList);
            mMusicListView.setAdapter(mMusicItemAdapter);
            mListTitle.setText(String.format(Locale.getDefault(), "%s: 共 %d 首",
                    mCurrentMusicList.getName(), mCurrentMusicList.size()));
            mBackImageView.setBackgroundResource(R.drawable.ic_back);
        }else {
            mMusicListAdapter.setList(mMusicLists);
            mMusicListView.setAdapter(mMusicListAdapter);
            mListTitle.setText("歌单: " + mMusicLists.size());
            mBackImageView.setBackgroundResource(R.drawable.ic_add_list);
        }
        mMusicListAdapter.notifyDataSetChanged();
        mMusicItemAdapter.notifyDataSetChanged();
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
        mMusicListCallback.onMusicListPrepare(mPlayingMusicList, 0);
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
        void onMusicListPrepare(MusicList<MusicItem> list, int position);
        void onMusicItemClick(MusicList<MusicItem> list, int position);
        void onSlideDirectionChange(boolean slideUp);
    }
}
