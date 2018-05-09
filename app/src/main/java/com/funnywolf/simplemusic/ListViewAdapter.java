package com.funnywolf.simplemusic;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by funnywolf on 18-5-8.
 */

public class ListViewAdapter extends BaseAdapter {

    private List<String> mList;
    private LayoutInflater inflater;
    private ViewHolder viewHolder;

    public ListViewAdapter(Context context, List<String> list) {
        mList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.music_item, null);
            viewHolder.mTextView = convertView.findViewById(R.id.music_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(mList.get(position));
        return convertView;
    }

    static class ViewHolder {
        private TextView mTextView;
    }
}
