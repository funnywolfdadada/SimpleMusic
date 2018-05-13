package com.funnywolf.simplemusic;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

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
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicListCallback.onMusicItemClickListener(mMusicList, position);
            }
        });
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
        if(cursor != null && cursor.moveToFirst()){
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


    public static interface MusicListCallback {
        void onMusicItemClickListener(List<MusicItem> list, int position);
    }
}
