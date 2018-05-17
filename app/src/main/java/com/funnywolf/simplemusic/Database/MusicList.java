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
        return list == null ? 0 : list.size();
    }

    public T get(int position) {
        return list.get(position);
    }
    public boolean add(T item) {
        String str;
        if(item == null || (str = item.toString()) == null)
            return false;
        if(set.contains(str))
            return false;
        set.add(str);
        list.add(item);
        return true;
    }
    public void remove(T item) {
        list.remove(item);
    }
    public void remove(int position) {
        list.remove(position);
    }

    @Override
    public String toString() {
        return name;
    }
}
