
// DEPRECATED. 

import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import ProfileData from '../api/ProfileData';
import axios from 'axios';

import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Avatar from '@mui/material/Avatar';
import Typography from '@mui/material/Typography';

function UserProfile() {
  const { username } = useParams();
  const [userData, setUserData] = useState(null);
  const [usernameNotFound, setUsernameNotFound] = useState(null);
  const [profileData, setProfileData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {

    // NOT USED

    const getData = async (username) => {
      try {
        const response = await axios.post('http://localhost:8080/stats/getstats', { username: username }, // currently auth and credentials are disabled as they should not be needed.
          {
            headers:  //
              { withCredentials: true }
          });
        console.log(response.data);
        if (response.data.responseMessage.includes("Error")) {
          setUsernameNotFound(true);
        } else {
          setUsernameNotFound(false);
          console.log("got profileData");
          const profileDataInstance = new ProfileData(
            response.data.username,
            response.data.gamesPlayed,
            response.data.gamesWon,
            response.data.favouriteArtist,
            response.data.favouriteArtistWins,
            response.data.favouriteArtistOccurances,
            response.data.responseMessage
          )
          setProfileData(profileDataInstance);
        }
        setLoading(false);
      } catch (error) {
        console.log("fuck this shit : " + error);
      }

    }

    getData(username);

  }, [username]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!profileData || usernameNotFound) {
    return <div>Error: No user found under that name. </div>;
  }

  return (
    <Box sx={styles.container}>
      <Paper elevation={3} sx={styles.island}>
        <Avatar
          alt={profileData.username}
          src={`https://ui-avatars.com/api/?name=${profileData.username}`}
          sx={styles.avatar}
        />
        <Typography variant="h4" component="div" gutterBottom>
          {profileData.username}
        </Typography>
        <Typography variant="body1" component="div">
          Games Played: {profileData.gamesPlayed}
        </Typography>
        <Typography variant="body1" component="div">
          Games Won: {profileData.gamesWon}
        </Typography>
        <Typography variant="body1" component="div">
          Favourite Artist: {profileData.favouriteArtist || "Not Set"}
          <Box ml={4} mt={1}>
            <Typography variant="body2" component="div">
              Wins: {profileData.favouriteArtistWins || 0}
            </Typography>
            <Typography variant="body2" component="div">
              Occurrences: {profileData.favouriteArtistOccurances || 0}
            </Typography>
            <Typography variant="body2" component="div">
              Win %: {
                profileData.favouriteArtistOccurances && profileData.favouriteArtistWins
                  ? ((profileData.favouriteArtistWins / profileData.favouriteArtistOccurances) * 100).toFixed(2)
                  : 0
              }%
            </Typography>
          </Box>
        </Typography>
        {/* Add more fields as needed */}
      </Paper>
    </Box>
  );
}

const styles = {
  container: {
    paddingTop: '60px',
    height: '100vh',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    background: '#f0f0f0',
  },
  island: {

    height: '90%',
    width: '90%',            // added this to adjust the width
    padding: '20px',
    borderRadius: '12px',
    background: '#ffffff',
    textAlign: 'left',
    overflow: 'auto'
  },
  avatar: {
    width: 'calc(60px + 5vw)',   // base size of 60px + 5% of viewport width
    height: 'calc(60px + 5vw)',  // same as above for height
    margin: '0 auto',
    marginBottom: '20px',
  },
};
 // TODO: Come back once basic game loop works.

export default UserProfile;
