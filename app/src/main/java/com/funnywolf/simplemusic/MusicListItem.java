package com.funnywolf.simplemusic;

import java.util.ArrayList;

public class MusicListItem {
    private String name;
    private ArrayList<MusicItem> musicList;

    public MusicListItem(String name) {
        this.name = name;
        this.musicList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MusicItem> getMusicList() {
        return musicList;
    }

    public void setMusicList(ArrayList<MusicItem> musicList) {
        this.musicList = musicList;
    }

    public int getCapacity() {
        return musicList == null ? 0 : musicList.size();
    }
}
