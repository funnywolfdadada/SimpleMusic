package com.funnywolf.simplemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MusicListItemAdapter extends BaseAdapter {

    private List<MusicListItem> mList;
    private LayoutInflater mLayoutInflater;

    public MusicListItemAdapter(Context context, List<MusicListItem> list) {
        mList = list;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setList(List<MusicListItem> list) {
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
            view = mLayoutInflater.inflate(R.layout.music_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.listNum = view.findViewById(R.id.list_num);
            viewHolder.listName = view.findViewById(R.id.list_name);
            viewHolder.listCapacity = view.findViewById(R.id.list_capacity);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MusicListItem list = mList.get(i);
        viewHolder.listNum.setText(String.valueOf(i + 1));
        viewHolder.listName.setText(list.getName());
        viewHolder.listCapacity.setText(String.format(Locale.getDefault(),
                "共 %d 首", list.getCapacity()));

        return view;
    }

    private class ViewHolder {
        private TextView listNum;
        private TextView listName;
        private TextView listCapacity;
    }
}

