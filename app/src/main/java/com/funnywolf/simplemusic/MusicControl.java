package com.funnywolf.simplemusic;

import java.util.List;

/**
 * Created by funnywolf on 18-5-5.
 */

public interface MusicControl {
    enum PlayMode{
        LIST_LOOP_MODE, RANDOM_MODE, SINGLE_LOOP_MODE
    }

    void play(List<MusicItem> list, int position);

    void start();
    void pause();
    void next();
    void prev();

    PlayMode getMode();
    void setMode(PlayMode mode);

    int getPosition();
    MusicItem getCurrentMusic();
    void seekTo(int msec);

    boolean isPlaying();
}
