class ProfileData {
    constructor(
        username = null, 
        gamesPlayed = null, 
        gamesWon = null, 
        favouriteArtist = null, 
        favouriteArtistWins = null, 
        favouriteArtistOccurances = null, 
        responseMessage = null
    ) {
        this.username = username;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.favouriteArtist = favouriteArtist;
        this.favouriteArtistWins = favouriteArtistWins;
        this.favouriteArtistOccurances = favouriteArtistOccurances;
        this.responseMessage = responseMessage;
    }
}

export default ProfileData;  