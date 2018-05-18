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

    public boolean contains(String name) {
        return set.contains(name);
    }

    public boolean add(T item) {
        String name;
        if(item == null || set.contains(name = item.toString()))
            return false;
        set.add(name);
        list.add(item);
        return true;
    }

    public void remove(T item) {
        if (item == null)
            return;
        list.remove(item);
        set.remove(item.toString());
    }

    public void remove(int position) {
        if(position < 0 || position >= list.size())
            return;
        set.remove(list.get(position).toString());
        list.remove(position);
    }

    public String[] getAllItemsName() {
        String[] names = new String[list.size()];
        for(int i = 0; i < list.size(); i++) {
            names[i] = list.get(i).toString();
        }
        return names;
    }

    @Override
    public String toString() {
        return name;
    }
}
