package com.funnywolf.simplemusic;

import java.util.Locale;

/**
 * Created by funnywolf on 18-5-7.
 */

public class MusicItem {
    public final long id;
    public final String name;
    public final String title;
    public final String artist;
    public final String path;
    public final String duration;
    public final int durationInt;
    public final String size;

    public int currentTime;

    public MusicItem(long id, String name, String title, String artist,
                     String path, int duration, long size){
        this.id = id;
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
}
