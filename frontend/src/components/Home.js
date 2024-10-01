import React from 'react';
import axios from 'axios';
import { Button, Card, CardContent, Typography, Grid, Container, Paper, List, ListItem, ListItemText, Link } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useTheme } from './ThemeContext';
import { create } from '@mui/material/styles/createTransitions';
import './Loaders.css';
import CustomCard from './UI Components/CustomCard';
import Button2 from './UI Components/Buttons/Button2';
import TitleCard from './UI Components/TitleCard';
import '../index.css';
import LegalNotification from './UI Components/LegalNotification';
import { ThemeContext } from '@emotion/react';


const Home = ({ userLoggedIn }) => {

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();

    const createRoom = async () => {
        try {
            let token = localStorage.getItem("jwtToken");

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };

            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/room/create`, { headers });
            const roomData = response.data;
            console.log(roomData);
            if (roomData.responseMessage.includes("[OLD_ROOM_DELETED]")) {
                localStorage.setItem('notification', roomData.notification);
                // Allows talking to app.js from same tab (removes limitation of the storage listeners)
                const event = new CustomEvent('notificationSet', { detail: roomData.notification });
                window.dispatchEvent(event);

            } else {
                navigate(`/lobby/${roomData.roomId}`);
            }



        } catch (error) {
            console.error('Error creating room:', error);

            if (error.response && error.response.status === 401) {
                localStorage.setItem('notification', "Authentication Expired|Your authentication had expired, the page has been reloaded and you are re-authorised");
                window.location.reload();
            }
            if (error.response && error.response.status === 500) {
                createRoom();
            }
        }
    };

    const createPractice = async () => {
        try {
            let token = localStorage.getItem("jwtToken");

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };

            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/room/practice`, { headers });
            const roomData = response.data;

            navigate(`/practice/${roomData.roomId}`);

        } catch (error) {
            // console.error('Error creating room:', error);

            if (error.response && error.response.status === 401) {
                localStorage.setItem('notification', "Authentication Expired|Your authentication had expired, the page has refreshed to attempt reauthorisation. you may need to relogin.");
                window.location.reload();

            }
            if (error.response && error.response.status === 500) {
                createPractice();
            }
        }
    };

    const goToResults = () => {
        navigate("/results");
    }

    const testNotification = () => {
        localStorage.setItem('notification', 'TestNofication|asdasdasdasdasdasasdasd asdasdasdasdasd');
        const event = new CustomEvent('notificationSet');
        window.dispatchEvent(event);
    }

    return (
        <Container maxWidth="false" style={{ height: '90vh', backgroundColor: '#1E1E1E' }}>
            <Grid container spacing={2} style={{ height: 'auto' }}>
                <Grid item xs={12} sx={{ mt: 2 }}>
                    <CustomCard >
                        <div style={{ margin: 10 }}>
                            <TitleCard title="Welcome to Lyric Labs">
                            </TitleCard>
                        </div>
                        <Typography variant="body1" component="p">
                            <React.Fragment>
                                Lyric Labs is the web-game developed for my undergraduate dissertation project. The underlying aim is to 'research' into what effect, if any, time-pressure and a competitive atmosphere have on memory recogition.
                            </React.Fragment>
                            <br></br>
                            <React.Fragment>
                                It was originally conceptualised as a game to prompt quick responses on which artist different lyrics belong to, and ended up as a plausible experiment to look into how timed multiplayer environments and the pressures they impose can effect peoples ability to answer quickly and correctly.
                            </React.Fragment>
                            <br></br>
                            <br></br>
                            <React.Fragment>
                                You can read below about how each mode works and its purpose, or create an account in the top right and jump in using the respective buttons.
                            </React.Fragment>
                        </Typography>

                    </CustomCard>
                </Grid>
                <Grid item xs={12} md={6}>
                    <CustomCard style={{}}>
                        <div className="TitleCardDiv">
                            <TitleCard title="Multiplayer">
                            </TitleCard>
                        </div>
                        <div style={{ marginTop: 10 }}>
                            {userLoggedIn ? (
                                <Button2 variant="contained" color="primary" onClick={createRoom} style={{ margin: 10 }}>
                                    Create Room
                                </Button2>
                            ) : (
                                <>
                                    <Button2 variant="contained" color="primary" disabled style={{ margin: 10 }}>
                                        Log in to create a room
                                    </Button2>
                                </>
                            )}
                        </div>
                        <Typography sx={{ color: theme.palette.text.secondary, textAlign: 'center', marginTop: 1 }}>
                            Requirements:
                            <List sx={{
                                color: theme.palette.text.secondary,
                                display: 'flex',
                                flexDirection: 'row', // This makes the list horizontal
                                justifyContent: 'center',
                                alignItems: 'center',
                                padding: 0 // Remove default padding to avoid alignment issues
                            }}>
                                <ListItem sx={{ display: 'inline-block', width: 'auto' }}>
                                    <Typography>2-10 Players</Typography>
                                </ListItem>
                                <ListItem sx={{ display: 'inline-block', width: 'auto' }}>
                                    <Typography>2+ Artists</Typography>
                                </ListItem>
                            </List>
                        </Typography>
                        <Typography variant="body1" component="p" className="typography-multiline" sx={{ m: 2 }}>
                            <React.Fragment>
                                Based around the popular room based approach most web games use, you can just create a room and then can send the link to people to join. The host controls all settings for the game.
                            </React.Fragment>
                            <br></br>
                            <br></br>
                            <React.Fragment>
                                As the host, you can control the allowed time to guess for each round, the number of players for the room (although you ultimately decide who has access by how you share the link), and the minimum and maximum number of songs, allowing for either a random choice between bounds or just enter the same in both for a specific amount. Finally, you also control the artist selection. There is a lower limit of 2, because without 2, it isn't much of a challenge, and currently, no upper limit; however, I recommend staying near or less than 5, just to prevent extensive load times at the start of a game. With enough artists, you will just completely break functionality.
                            </React.Fragment>
                            <br></br>
                            <br></br>
                            <React.Fragment>
                                After the Game, you will have the opportunity to view graphs to display the data you all just participated in collecting, and can even view some specific player-specific performance graphs. This is an insight into the analysis being done for my dissertation project, with more extensive graphs that utilize all collected data available at the results page.
                            </React.Fragment>
                            <br></br>
                            {/* <br></br>
                            <React.Fragment>
                                Happy Guessing.
                            </React.Fragment> */}

                        </Typography>


                    </CustomCard>
                </Grid>
                <Grid item xs={12} md={6}>
                    <CustomCard>
                        <div className="TitleCardDiv">
                            <TitleCard title="Solo Practice">
                            </TitleCard>
                        </div>
                        <div style={{ marginTop: 10 }}>
                            {userLoggedIn ? (
                                <Button2 variant="contained" color="primary" onClick={createPractice} style={{ margin: 10 }}>
                                    Start Practice
                                </Button2>
                            ) : (
                                <>
                                    <Button2 variant="contained" color="primary" disabled style={{ margin: 10 }}>
                                        Log in to access practice
                                    </Button2>
                                </>
                            )}
                        </div>

                        <Typography sx={{ color: theme.palette.text.secondary, textAlign: 'center', marginTop: 1 }}>
                            Requirements:
                            <List sx={{
                                color: theme.palette.text.secondary,
                                display: 'flex',
                                flexDirection: 'row', // This makes the list horizontal
                                justifyContent: 'center',
                                alignItems: 'center',
                                padding: 0 // Remove default padding to avoid alignment issues
                            }}>
                                <ListItem sx={{ display: 'inline-block', width: 'auto' }}>
                                    <Typography>1 Player</Typography>
                                </ListItem>
                                <ListItem sx={{ display: 'inline-block', width: 'auto' }}>
                                    <Typography>2+ Artists</Typography>
                                </ListItem>
                            </List>
                        </Typography>

                        <Typography variant="body1" component="p" className="typography-multiline" sx={{ m: 2 }}>
                            <React.Fragment>
                                Here, the time limits are removed to shift the focus from acting fast, towards getting the correct answer first time. This adjusts the game loop a little, instead of guessing multiple times until getting the right artist, you only get one chance for each before moving onto the next. (Feedback on your answer will appear in the history tab).
                            </React.Fragment>
                            <br></br>
                            <br></br>
                            <React.Fragment>
                                After the round, you will get the same option to view statistics for the round you just played; however, due to most of the graphs being based around player-player stats, one section is removed and just the player stats are shown.
                            </React.Fragment>
                            <br></br>
                            <br></br>
                            <React.Fragment>
                                While this section may seem less exciting, it is beneficial for the results as like every experiment, a baseline is required to compare to, so be sure to try this part out.
                            </React.Fragment>
                        </Typography>

                    </CustomCard>
                </Grid>

                {/* Data Information Section */}
                <Grid item xs={12}>
                    <CustomCard>
                        <div style={{ margin: 10 }}>
                            <TitleCard title="Data Collection & Gathering">
                            </TitleCard>
                        </div>
                        <Grid container spacing={2}>
                            <Grid item xs={12} md={4}>
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                    <a href="https://www.spotify.com" target="_blank" rel="noopener noreferrer">
                                        <img src={`${process.env.PUBLIC_URL}/spotify-full-green.png`} alt="Spotify Logo" style={{ height: '5rem' }} />
                                    </a>
                                </div>
                                <Typography variant="body1" component="p" sx={{ m: 2 }}>
                                    Spotify's API provides everything that you see about artists, from the original search you do to add them to a game's artist list, and you see their profile picture next to their name, all the way to that annoying wait time at the start of a round where it's getting the game ready, I am constantly calling their API to get the data. Thanks to Spotify, this is all free and they have a very generous cap on its usage, so if bugs do arise, it's probably developer error.
                                </Typography>
                            </Grid>
                            <Grid item xs={12} md={4}>
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                    <a href="https://www.musixmatch.com" target="_blank" rel="noopener noreferrer">
                                        <img src={`${process.env.PUBLIC_URL}/Musixmatch-logo-panoramic-red.png`} alt="panoramic" style={{ height: '5rem' }} />
                                    </a>
                                </div>
                                <Typography variant="body1" component="p" sx={{ m: 2 }}>
                                    The one thing that Spotify doesn't provide about songs is their lyrics, which is where Musixmatch comes in. For each song, once it's selected, I attempt to get the associated lyrics from their database, and can then use an algorithm to try and pick some useful and understandable lyrics. This can take a while, especially if songs do not consist of any usable lyrics within the first 30% of the song, as that is all that Musixmatch's free usage allows for, which then requires song reselection and another attempt. So if your game takes a while to prepare, it's probably due to some songs being unusable or just how many it's had to sort through.
                                </Typography>
                            </Grid>
                            <Grid item xs={12} md={4}>
                                <Typography variant="h5" component="p" sx={{ m: 2 }}>
                                    What I Collect
                                </Typography>
                                <Typography variant="body1" sx={{ m: 2 }}>
                                    As you will/have agreed to when making an account, I collect data specifically for analysis purposes to provide both the graphs in the post-game stats page, but also for results page to display trends against / towards my hypothesis. For each guess I collect the time to respond and if its correct.
                                </Typography>
                                <Button2 variant="contained" color="primary" onClick={goToResults} style={{ margin: 10 }}>
                                    See Results
                                </Button2>
                            </Grid>
                            <Grid item xs={12} md={8}>
                                <Typography variant="h5" component="p" sx={{ m: 2 }}>
                                    Legal Obligations
                                </Typography>
                                <Typography variant="body1" sx={{ m: 2 }}>
                                    Because of rules set in place the owners of the APIs explained above, you will see the following display on any page that displays information recieved from them.
                                </Typography>
                                <LegalNotification spotify={true} musixmatch={true}></LegalNotification>
                            </Grid>
                        </Grid>
                    </CustomCard>
                </Grid>
            </Grid >
        </Container>
    );
};

export default Home;

// TODO: 

/* 
- Artists not clickable on game (live only) --> pretty sure its fixed just not pushed
- Negative fields on input boxs not autocorrected (both)
- Signup button no longer redirects, just change to 2 buttons probs. 


- Implement Switching of what is and game descriptions if user is logged in.
- Fix stupid white space error at the bottom.
- Add a questions / notes section for explanations for specific interactions.
*/
