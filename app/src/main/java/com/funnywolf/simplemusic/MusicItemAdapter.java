package com.funnywolf.simplemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.funnywolf.simplemusic.Database.MusicItem;

import java.util.List;

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

    public void setList(List<MusicItem> list) {
        mList = list;
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
            viewHolder.musicNum = view.findViewById(R.id.music_num);
            viewHolder.musicTitle = view.findViewById(R.id.music_title);
            viewHolder.musicName = view.findViewById(R.id.music_name);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MusicItem music = mList.get(i);
        viewHolder.musicNum.setText(String.valueOf(i + 1));
        viewHolder.musicTitle.setText(music.title);
        viewHolder.musicName.setText(music.name);

        return view;
    }

    private class ViewHolder {
        private TextView musicNum;
        private TextView musicTitle;
        private TextView musicName;
    }
}
