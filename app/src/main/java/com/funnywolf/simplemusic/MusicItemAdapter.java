package com.funnywolf.simplemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by dell on 2018/5/9.
 */

public class MusicItemAdapter extends BaseAdapter {

    private List<MusicItem> mList;
    private LayoutInflater mLayoutInflater;
    private ViewHolder mViewHolder;

    public MusicItemAdapter(Context context, List<MusicItem> list) {
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList == null ? null : mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.music_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.musicTitle = view.findViewById(R.id.music_title);
            mViewHolder.musicArtist = view.findViewById(R.id.music_artist);
            mViewHolder.musicName = view.findViewById(R.id.music_name);
            mViewHolder.musicDuration = view.findViewById(R.id.music_duration);
            mViewHolder.musicSize = view.findViewById(R.id.music_size);
            view.setTag(mViewHolder);
        }else {
            mViewHolder = (ViewHolder)view.getTag();
        }

        MusicItem music = mList.get(i);
        mViewHolder.musicTitle.setText((i + 1) + ": " + music.getTitle());
        mViewHolder.musicArtist.setText(music.getArtist());
        mViewHolder.musicName.setText(music.getName());
        int duration = music.getDuration() / 1000;
        mViewHolder.musicDuration.setText(duration / 60 + ":" + duration % 60);
        long size = music.getSize();
        mViewHolder.musicSize.setText(size/1024/1024+"MB");

        return view;
    }

    private class ViewHolder {
        private TextView musicTitle;
        private TextView musicArtist;
        private TextView musicName;
        private TextView musicDuration;
        private TextView musicSize;
    }
}
