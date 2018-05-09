package com.funnywolf.simplemusic;

/**
 * Created by funnywolf on 18-5-5.
 */

public interface MusicControl {
    enum PlayMode{
        LIST_LOOP_MODE, RANDOM_MODE, SINGLE_LOOP_MODE
    };
    boolean play(String path);
    void stop();
    void start();
    void pause();
    void next();
    void prev();
    void changeMode(PlayMode mode);
    int getCurrentPosition();
    int getDuration();
    void seekTo(int msec);
    MusicItem getCurrentMusic();
}
