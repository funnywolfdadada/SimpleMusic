package com.funnywolf.simplemusic;

/**
 * Created by funnywolf on 18-5-7.
 */

public class MusicItem {
    private String name;
    private String artist;
    private String title;
    private String path;
    int duration;
    long size;

    public MusicItem(){}
    public MusicItem(String name, String title, String artist, String path, int duration, long size){
        this.name = name;
        this.title = title;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
        this.size =size;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getArtist() {
        return artist;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public String toString() {
        return String.format("name: %s, title: %s, artist: %s, duration: %d, size: %d, path: %s"
                , name, title, artist, duration, size, path);
    }
}
