package com.funnywolf.simplemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by dell on 2018/5/9.
 */

public class MusicItemAdapter extends BaseAdapter {

    private List<MusicItem> mList;
    private LayoutInflater mLayoutInflater;

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
        ViewHolder viewHolder;
        if(view == null) {
            view = mLayoutInflater.inflate(R.layout.music_item, null);
            viewHolder = new ViewHolder();
            viewHolder.musicTitle = view.findViewById(R.id.music_title);
            viewHolder.musicArtist = view.findViewById(R.id.music_artist);
            viewHolder.musicName = view.findViewById(R.id.music_name);
            viewHolder.musicDuration = view.findViewById(R.id.music_duration);
            viewHolder.musicSize = view.findViewById(R.id.music_size);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MusicItem music = mList.get(i);
        viewHolder.musicTitle.setText(String.format(Locale.getDefault(),
                "%d: %s", (i + 1), music.title));
        viewHolder.musicArtist.setText(music.artist);
        viewHolder.musicName.setText(music.name);
        viewHolder.musicDuration.setText(music.duration);
        viewHolder.musicSize.setText(music.size);

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
