package com.funnywolf.simplemusic;

import java.util.ArrayList;

public class MusicListItem {
    private String name;
    private ArrayList<MusicItem> musicList;
    private boolean isCurrentList;

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

    public void setCurrentList(boolean currentList) {
        isCurrentList = currentList;
    }

    public boolean isCurrentList() {
        return isCurrentList;
    }
}
