package com.project.app.api;

import com.project.app.model.music.Song;
import org.springframework.web.servlet.function.RouterFunctionDsl;

public class WrappedRoundInformation {

    Song song;
    RoundInformation roundInformation;

    public WrappedRoundInformation(Song song, RoundInformation roundInformation) {
        this.song = song;
        this.roundInformation = roundInformation;
    }
}
