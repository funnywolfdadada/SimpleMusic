package com.funnywolf.simplemusic;

import java.util.Locale;

/**
 * Created by funnywolf on 18-5-7.
 */

public class MusicItem {
    public final MusicListItem musicList;

    public final long id;
    public final String name;
    public final String title;
    public final String artist;
    public final String path;
    public final String duration;
    public final int durationInt;
    public final String size;

    public int currentTime;

    public MusicItem(MusicListItem list, long id, String name, String title, String artist,
                     String path, int duration, long size){
        this.id = id;
        this.musicList = list;
        this.name = name;
        this.title = title;
        this.artist = artist;
        this.path = path;
        durationInt = duration;
        duration /= 1000;
        this.duration = String.format(Locale.getDefault(),
                "%d:%02d", duration / 60, duration % 60);
        this.size = String.format(Locale.getDefault(),
                "%.2fMB", size / 1024d / 1024d);
        currentTime = 0;
    }

    public MusicItem(MusicListItem list, MusicItem music) {
        this.musicList = list;
        this.id = music.id;
        this.name = music.name;
        this.title = music.title;
        this.artist = music.artist;
        this.path = music.path;
        this.durationInt = music.durationInt;
        this.duration = music.duration;
        this.size = music.size;
        currentTime = 0;
    }

}
