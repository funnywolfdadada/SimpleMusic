package com.funnywolf.simplemusic;

import com.funnywolf.simplemusic.Database.MusicItem;

import java.util.List;

/**
 * Created by funnywolf on 18-5-5.
 */

public interface MusicControl {
    enum PlayMode{
        LIST_LOOP_MODE, RANDOM_MODE, SINGLE_LOOP_MODE
    }

    void setMusicList(List<MusicItem> list);
    void setCurrentPosition(int position);
    int getCurrentPosition();

    MusicItem getCurrentMusic();

    void prepare();
    void play();
    void pause();
    void next();
    void prev();

    PlayMode getMode();
    void setMode(PlayMode mode);

    void seekTo(int msec);

    boolean isPlaying();
}
