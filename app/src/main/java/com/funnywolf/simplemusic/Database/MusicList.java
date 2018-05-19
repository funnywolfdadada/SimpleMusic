package com.funnywolf.simplemusic.Database;

import com.funnywolf.simplemusic.Database.MusicItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MusicList<T> {
    private String name;
    private boolean isPlaying;
    private ArrayList<T> list = new ArrayList<>();
    private Map<String, T> map = new LinkedHashMap<>();

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
        if(position < 0 || position >= list.size())
            return null;
        return list.get(position);

    }

    public T get(String name) {
        if(name != null && map.containsKey(name)) {
            return map.get(name);
        }
        return null;
    }

    public boolean contains(String name) {
        return map.containsKey(name);
    }

    public int indexOf(T item) {
        return list.indexOf(item);
    }

    public boolean add(T item) {
        String name;
        if(item == null || map.containsKey(name = item.toString()))
            return false;
        map.put(name, item);
        list.add(item);
        return true;
    }

    public void remove(T item) {
        if (item == null)
            return;
        list.remove(item);
        map.remove(item.toString());
    }

    public void remove(int position) {
        if(position < 0 || position >= list.size())
            return;
        map.remove(list.get(position).toString());
        list.remove(position);
    }

    public boolean rename(int position, String name) {
        if(position < 0 || position >= list.size() || map.containsKey(name))
            return false;
        map.remove(list.get(position).toString());
        map.put(name, list.get(position));
        return true;
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
