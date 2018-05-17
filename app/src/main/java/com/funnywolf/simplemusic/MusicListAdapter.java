package com.funnywolf.simplemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.funnywolf.simplemusic.Database.MusicItem;
import com.funnywolf.simplemusic.Database.MusicList;

import java.util.List;
import java.util.Locale;

public class MusicListAdapter extends BaseAdapter {

    private MusicList<MusicList<MusicItem>> mList;
    private LayoutInflater mLayoutInflater;

    public MusicListAdapter(Context context, MusicList<MusicList<MusicItem>> list) {
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setList(MusicList<MusicList<MusicItem>> list) {
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
            view = mLayoutInflater.inflate(R.layout.music_list, null);
            viewHolder = new ViewHolder();
            viewHolder.listNum = view.findViewById(R.id.list_num);
            viewHolder.listName = view.findViewById(R.id.list_name);
            viewHolder.listCapacity = view.findViewById(R.id.list_capacity);
            viewHolder.listPlaying = view.findViewById(R.id.list_playing);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MusicList list = mList.get(i);
        viewHolder.listNum.setText(String.valueOf(i + 1));
        viewHolder.listName.setText(list.getName());
        viewHolder.listCapacity.setText(String.format(Locale.getDefault(),
                "共 %d 首", list.size()));
        if(list.isPlaying()) {
            viewHolder.listPlaying.setVisibility(View.VISIBLE);
        }else {
            viewHolder.listPlaying.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private class ViewHolder {
        private TextView listNum;
        private TextView listName;
        private TextView listCapacity;
        private ImageView listPlaying;
    }
}

