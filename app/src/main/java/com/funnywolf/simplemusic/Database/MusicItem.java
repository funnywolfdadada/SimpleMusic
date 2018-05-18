package com.funnywolf.simplemusic.Database;

import java.util.Locale;

/**
 * Created by funnywolf on 18-5-7.
 */

public class MusicItem {
    private final long id;
    private final String name;
    private final String title;
    private final String artist;
    private final String path;
    private final String duration;
    private final int durationInt;
    private final String size;

    private int currentTime;

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

    public String getName() {
        return name;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getDurationInt() {
        return durationInt;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return title;
    }
}
