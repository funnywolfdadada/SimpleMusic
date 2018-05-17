package com.funnywolf.simplemusic.Database;

import com.funnywolf.simplemusic.Database.MusicItem;

import java.util.ArrayList;

public class MusicListItem {
    private String name;
    private ArrayList<MusicItem> musicList;
    private boolean isPlaying;

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

    public int getCapacity() {
        return musicList == null ? 0 : musicList.size();
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
