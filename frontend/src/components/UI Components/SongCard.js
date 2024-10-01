// https://uiverse.io/ahmed150up/quiet-goat-67
import React from 'react';
import './SongCard.css';

const SongCard = ({ currentArtistName, currentSongName, maxGuessTime, photoUrl }) => {
    const animationDuration = `${maxGuessTime}s`;

    return (
        <div className="audio-player">
            <div className="album-cover" style={{ backgroundImage: photoUrl ? `url(${photoUrl})` : 'none' }}></div>
            <div className="player-controls">
                <div className="song-info">
                    <div className="song-title">{currentSongName}</div>
                    <p className="artist">{currentArtistName}</p>
                </div>
                <div className="progress-bar">
                    <div className="progress" style={{ animationDuration }}></div>
                </div>
            </div>
        </div>
    );
};

export default SongCard;

