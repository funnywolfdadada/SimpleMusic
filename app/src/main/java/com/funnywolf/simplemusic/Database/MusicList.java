package com.funnywolf.simplemusic.Database;

import com.funnywolf.simplemusic.Database.MusicItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MusicList<T> {
    private String name;
    private boolean isPlaying;
    private ArrayList<T> list = new ArrayList<>();
    private Set<String> set = new HashSet<>();

    public MusicList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int size() {
        return list.size();
    }

    public T get(int position) {
        return list.get(position);
    }

    public boolean contains(T item) {
        String str;
        return (item != null) && ((str = item.toString()) != null) && set.contains(str);
    }

    public boolean add(T item) {
        if(contains(item))
            return false;
        set.add(item.toString());
        list.add(item);
        return true;
    }

    public void remove(T item) {
        list.remove(item);
    }

    public void remove(int position) {
        list.remove(position);
    }

    public String[] getItems() {
        String[] items = new String[list.size()];
        for(int i = 0; i < list.size(); i++) {
            items[i] = list.get(i).toString();
        }
        return items;
    }

    @Override
    public String toString() {
        return name;
    }
}
