import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useTheme } from './ThemeContext.js';
import { Button, List, ListItem, Paper, LinearProgress, MenuItem, MenuList, Avatar, IconButton, Typography, TextField, Slider, Switch, Grid, FormControlLabel, Hidden } from '@mui/material';
import Scoreboard from './Scoreboard';
import History from './History';
import { useNavigate } from 'react-router-dom';
import { CenterFocusStrong, UpdateRounded } from '@mui/icons-material';
import SideCard from './UI Components/SideCard.js';
import TitleCard from './UI Components/TitleCard.js';
import LegalNotification from './UI Components/LegalNotification.js';
import Button2 from './UI Components/Buttons/Button2.js';
import DefaultTooltip from './UI Components/DefaultTooltip.js';
import ConnectionTooltip from './UI Components/ConnectionTooltip.js';

const Game = () => {
    const { roomID } = useParams();

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();

    const stompClientRef = useRef(null); // using ref helps to prevent it being reset of rendering. 
    const connectRef = useRef(null);
    const [connected, setConnected] = useState(false); // dont really know why this is here when connectRef exists but its used in lobby without connectRef being mentioned nearby so its needed. 06/12/23
    const [playerName, setPlayerName] = useState(null);
    const [lobbyName, setLobbyName] = useState(null);

    const [isHost, setIsHost] = useState(false);
    const [isGameReady, setIsGameReady] = useState(false);
    const [isPlaying, setIsPlaying] = useState(false);
    const [needsToCountdown, setNeedsToCountdown] = useState(false);
    const [isGameOver, setIsGameOver] = useState(false);

    const [roundNumber, setRoundNumber] = useState(0);
    const [totalRounds, setTotalRounds] = useState(0);
    const [maxGuessTime, setMaxGuessTime] = useState(0); // using 1 avoids a divide by 0 error on the progress calculations.
    const [lastLyricRecieveTimeMS, setLastLyricReceieveTimeMS] = useState(0);
    const [guessNumber, setGuessNumber] = useState(0);
    const [lastGuessTime, setLastGuessTime] = useState(null);
    const [currentLyricID, setCurrentLyricID] = useState(null);
    const [rawLyricInfo, setRawLyricInfo] = useState(null);
    const [canGuess, setCanGuess] = useState(false);
    const [triggerAutoReset, setTriggerAutoReset] = useState(false); // for timer
    const [correctGuess, setCorrectGuess] = useState(false);

    // timer display
    const [currentArtistName, setCurrentArtistName] = useState(null);
    const [currentSongName, setCurrentSongName] = useState(null);
    const [currentArtistPhotoURL, setCurrentArtistPhotoURL] = useState(null);

    // artistDisplay and related variables.
    const [artistList, setArtistList] = useState(null);

    const [users, setUsers] = useState([]);
    const [playerCount, setPlayerCount] = useState(null);
    const [mainTextContent, setMainTextContent] = useState("Waiting for players to connect...");
    const [countdown, setCountdown] = useState(5); // 5 is default wait time value, which is the first countdown that will be used when the game is starting. 
    const [guessTimeout, setGuessTimeout] = useState(false);

    const [historyRecord, setHistoryRecord] = useState(null);

    const [savingGameData, setSavingGameData] = useState(false);
    const TIMER_TIME = 2000;
    const [savingGameCountdown, setSavingGameCountdown] = useState(TIMER_TIME);

    const [userAnswer, setUserAnswer] = useState("");
    let timeout;
    let interval;

    // for MUI progress bar https://mui.com/material-ui/react-progress/
    const MIN = 0;
    const normalise = (value) => ((value - MIN) * 100) / ((maxGuessTime) - MIN);

    const closeWebSocket = useCallback(() => { // useCallBack() prevents unessecary rerendering of functions / compoenents or something. 
        if (stompClientRef && connectRef.current) {
            console.log("DEACTIVATING CLIENT");
            stompClientRef.current.deactivate();
            stompClientRef.current = null;
            connectRef.current = null;
            clearInterval(interval);
            clearTimeout(timeout);
            console.log("WebSocket closed.");
            localStorage.setItem('notification', 'Socket Closed|The connection for the room was ended');
            window.location.href = '/lyriclabs'; // TODO: uncomment
        } else {
            console.log("not recived a response yet, so no need to close webSocket");
        }
    }, []);

    function leaveLobby() {
        console.log("LEAVING LOBBY");
        if (stompClientRef.current && connectRef.current) {
            stompClientRef.current.publish({
                destination: "/app/disconnect",
            });
            console.log("sent disconnection message to backend");
            closeWebSocket();
            // navigate('/');
            window.location.href = '/lyriclabs';
        }
    }

    function handleArtistClick(artist) {
        if (canGuess == true) {
            let currentTime = new Date().getTime();
            let guessTimeMS = 0;
            let totalGuessTimeMS = currentTime - lastLyricRecieveTimeMS;
            if ((guessNumber + 1) > 1) {
                guessTimeMS = currentTime - lastGuessTime;
            } else {
                guessTimeMS = totalGuessTimeMS;
            }

            console.log('Artist clicked:', artist);
            if (stompClientRef.current && isPlaying) {
                const guessData = {
                    roomID: roomID,
                    lyricID: currentLyricID,
                    artistID: artist.spotifyID,
                    guessNumber: (guessNumber + 1),
                    guessTimeMS: guessTimeMS,
                    totalGuessTimeMS: totalGuessTimeMS,
                    roundNumber: roundNumber
                };
                stompClientRef.current.publish({
                    destination: "/app/sendGuess",
                    body: JSON.stringify(guessData)
                });
                // console.log(guessData);
                // console.log("guess should of been sent to backend.")
                setLastGuessTime(currentTime);
                setGuessNumber(guessNumber + 1); // changed at the end to hopefully prevent issues with it not being used
            }
        }
    }

    function handlePostGameAnalysis() {
        window.location.href = `lyriclabs/game-stats/${roomID}`;
    }

    const handleNewHistory = (newData) => {
        setHistoryRecord(prevHistory => {
            // Ensure prevHistory is an array -> fancy javascript syntax ooh.
            const history = prevHistory || [];
            const updatedHistory = [...history, newData];

            if (updatedHistory.length > 10) {
                return updatedHistory.slice(-10); // to remove oldest item
            }
            return updatedHistory;

        });
    };

    const updateSongInfo = (newArtist, newSong, newPhotoUrl) => {
        setCurrentArtistName(newArtist);
        setCurrentSongName(newSong);
        setCurrentArtistPhotoURL(newPhotoUrl);
    };

    const disableGuessTemporarily = () => {
        setCanGuess(false);
        setTriggerAutoReset(true);
    };

    // saving game timer
    useEffect(() => {
        let timerId;

        if (savingGameData && savingGameCountdown > 0) {
            timerId = setTimeout(() => {
                setSavingGameCountdown(savingGameCountdown - 100);
            }, 100);
        } else if (savingGameCountdown === 0) {
            console.log("Unlocked button");
            setMainTextContent("Game has ended. You can view your stats below");
            setNeedsToCountdown(false);
            setCountdown("");
            setSavingGameData(false);
            setSavingGameCountdown(0);
        }

        return () => clearTimeout(timerId);
    }, [savingGameData, savingGameCountdown]);

    useEffect(() => {
        let timer;
        if (!canGuess && triggerAutoReset) {
            console.log((maxGuessTime * 1000) * 0.2);
            // Wait % of max guess time second, then set canGuess back to true
            timer = setTimeout(() => {
                setCanGuess(true);
                setTriggerAutoReset(false);
            }, (maxGuessTime * 1000) * 0.2);
        }

        return () => clearTimeout(timer);
    }, [canGuess, triggerAutoReset]);

    useEffect(() => {
        if (users == []) {
            console.log("Users is empty");
        }
    }, [users])


    useEffect(() => {
        const connectedUsersCount = users.filter(user => user.isConnected).length;
        if (!isGameReady) {
            setMainTextContent(`Waiting for players to connect... (${connectedUsersCount}/${playerCount})`);
        }
        if (connectedUsersCount == playerCount && !isPlaying) {
            setMainTextContent("Game Preperation: Loading Albums...");
        }
    }, [users, playerCount, isGameReady]);

    useEffect(() => {
        if (needsToCountdown && countdown > 0) {
            const timerId = setTimeout(() => setCountdown(countdown - 1), 1000);
            return () => clearTimeout(timerId);
        } else {
            setNeedsToCountdown(false);
        }

        // prevents guessing when < 1
        if (countdown < 1) {
            setCanGuess(false);
            setGuessTimeout(true);
        }

    }, [countdown, needsToCountdown]);

    useEffect(() => {

        const HEARTBEAT_MS = 5000;

        const connectToWebSocket = () => {
            if (stompClientRef.current) {
                console.log("already connected... no need to do it all again");
            } else {
                console.log("connecting...");
                const jwtToken = localStorage.getItem("jwtToken");
                const socket = new SockJS(`${process.env.REACT_APP_SERVER_URL}/ws-endpoint?token=${jwtToken}`); // This might not work if a user loads up a link while their token is expired. TODO: Logic for comment to left. 
                const client = new Client({
                    webSocketFactory: () => socket,
                    heartbeatIncoming: 5000,
                    heartbeatOutgoing: 5000,
                    // debug: (str) => {
                    // console.log(str); // Keeping here to add back if needed.
                    // },
                });

                client.onConnect = () => {
                    // Subscribing to public messages
                    client.subscribe(`/topic/gameJoined/` + roomID, (message) => {
                        if (message.body) {
                            const responseBody = JSON.parse(message.body);
                            console.log('Received:', responseBody);
                            if (responseBody.responseMessage.includes("Error : Room does not exist.")) {
                                console.log("Room does not exist, closing connection and redirecting user");
                                closeWebSocket();
                                localStorage.setItem('notification', responseBody.notification);
                                // navigate('/');
                                window.location.href = '/lyriclabs';
                            }
                            if (responseBody.responseMessage.includes("has joined the Room")) {
                                const isHost = responseBody.responseMessage.startsWith("[H]");
                                console.log(responseBody.userList);
                                setUsers(responseBody.userList);
                                console.log(users);
                            } if (responseBody.responseMessage.includes("has connected")) {
                                console.log(responseBody.userList);
                                setPlayerCount(responseBody.playerCount);
                                setUsers(responseBody.userList);
                                if (responseBody.responseMessage.includes("[ALL_CONNECTED]" && !isPlaying)) {
                                    setMainTextContent("Game Preperation: Loading Albums...");
                                }

                                // console.log(users); // --> sadly printing here just prints empty cause line above hasnt taken effect yet. stupid react problems ay.
                            } if (responseBody.responseMessage.includes("[START_GAME_TIMER]")) {
                                setIsGameReady(true); // starts timer using useEffect.
                                setCountdown(5); // technically this is already pre-set but seems like a good approach to set it here to avoid confusion. 
                                setNeedsToCountdown(true);
                            }

                            if (responseBody.responseMessage.includes("force-closed")) {
                                localStorage.setItem('notification', 'Force Closure|The lobby you were in seems to of been force-closed. sorry about that');
                                closeWebSocket();
                                // navigate('/');
                                window.location.href = '/lyriclabs';
                            }
                            if (responseBody.responseMessage.includes("left the game")) {
                                // TODO: implement backend call relating to and frontend logic. Just Darken scoreboard entry or something similar to note they are gone. 
                                console.log(users);
                                setUsers(responseBody.currentPlayers);
                                if (responseBody.responseMessage.includes(playerName)) {
                                    // user has probably gone idle for too long (only known cause of this happening 22/09/23)
                                    // needs to be intended behaviour to prevent unknown interactions --> change if new approach is thought of.
                                    console.log("triggering leaveLobby via joinRoomed left the lobby if statement (29/09/23)");
                                    localStorage.setItem('notification', 'Idle Activity|You were removed from the room due to being idle');
                                    leaveLobby();
                                }
                            }
                            if (responseBody.responseMessage.includes("lost connection")) {
                                // TODO: handle things accordingly like showing a connection lost symbol. 
                                // TODO: find a way to access the isConnected stat of userlist and set theirs to false. this would handle the rendering of the connection symbol later on. 
                                if (responseBody.responseMessage.includes(playerName)) {
                                    setConnected(false);
                                }
                            }
                            if (responseBody.responseMessage.includes("New host")) {
                                // remake the new hosts rectangle to change from userRectangle to hostComponent. // newHost = responseBody.username;
                                const newHostUsername = responseBody.username; // Assuming this is how you get the new host's username
                                setUsers(responseBody.userList);
                                setLobbyName(responseBody.roomName);
                            }
                            if (responseBody.responseMessage.includes("[LOADING_RESOURCES]")) {
                                setMainTextContent("Game Preperation: Loading Rescources...");
                            }
                            if (responseBody.responseMessage.includes("[LOADING_ALBUMS]")) {
                                setMainTextContent("Game Preperation: Loading artists albums...");
                            }
                            if (responseBody.responseMessage.includes("[SELECTING_SONGS]")) {
                                setMainTextContent(`Game Preperation: Selecting songs (${responseBody.songNumber}/${responseBody.songCount})...`);
                            }
                        }
                    });

                    client.subscribe(`/topic/gameLoop/` + roomID, (message) => {
                        if (message.body) {
                            const responseBody = JSON.parse(message.body);
                            console.log('Received:', responseBody);
                            if (responseBody.responseMessage.includes("[NEW_ROUND]")) {
                                if (!isPlaying) {
                                    setIsPlaying(true);
                                }
                                // grab and allocate information.
                                updateSongInfo("???", "???", "");
                                setGuessNumber(0); // resets guess count for new round.
                                setCanGuess(true);
                                setRawLyricInfo(responseBody);
                                setLastLyricReceieveTimeMS(new Date().getTime());
                                setRoundNumber(responseBody.roundNumber);
                                setTotalRounds(responseBody.totalRounds);
                                setMainTextContent(responseBody.lyrics);
                                setCurrentLyricID(responseBody.lyricID);
                                setCountdown(responseBody.maxGuessTime);
                                setMaxGuessTime(responseBody.maxGuessTime);
                                setGuessTimeout(false);
                                setNeedsToCountdown(true);

                            } if (responseBody.responseMessage.includes("[GAME_END]")) {
                                console.log(responseBody.responseMessage);
                                // setIsPlaying(false); --> not really appropriate cause it breaks all the UI condtions. 
                                setMainTextContent("Game has ended. Saving data...");
                                setNeedsToCountdown(false);
                                setCountdown("");
                                setIsGameOver(true);
                                setSavingGameData(true);
                            }
                            if (responseBody.responseMessage.includes("[ARTIST_LIST_RECIEVED]")) {
                                // idk why I wrote this here, cant even find where I send it on the backend. keeping as a reminder this might be important

                            } if (responseBody.responseMessage.includes("[NEW_HISTORY]")) {
                                handleNewHistory(responseBody);
                                setUsers(responseBody.userList); // really tried to be efficient, but to no avail so sending whole user list over, again.

                            }
                        }
                    });

                    // Subscribing to private messages for the specific client. 
                    client.subscribe(`/user/queue/privateMessage`, (message) => {
                        if (message.body) {
                            const responseBody = JSON.parse(message.body);
                            console.log('Private message received:', responseBody);
                            if (responseBody.responseMessage === "Joined Game.") {
                                console.log("connectRef = true");
                                connectRef.current = true;
                                setConnected(true);
                                setLobbyName(responseBody.roomName);
                                setPlayerName(responseBody.username);
                                setUsers(responseBody.userList);
                                setArtistList(responseBody.artistList);
                            }

                            if (responseBody.responseMessage.includes("[SCORE_PERSONAL]")) {
                                if (responseBody.correct == true) {
                                    setCanGuess(false);
                                    setCorrectGuess(true);
                                    updateSongInfo(responseBody.currentArtistName, responseBody.currentSongName, responseBody.currentArtistPhotoURL);
                                    console.log("set can guess false");
                                } else {
                                    setCorrectGuess(false);
                                    disableGuessTemporarily();
                                }
                            }
                            if (responseBody.responseMessage.includes("[RECONNECTION_ROUND_INFO]")) {
                                if (!isPlaying) {
                                    setIsPlaying(true);
                                }
                                // grab and allocate information.
                                setGuessNumber(0); // resets guess count for new round.
                                if (responseBody.lyrics == null) {
                                    setIsGameReady(true);
                                    setIsPlaying(false);
                                    setCountdown("Unknown due to page refresh. Be ready.");
                                    setMainTextContent("Main Text Content")
                                    setNeedsToCountdown(false);
                                    setCanGuess(false);
                                } else {
                                    setCanGuess(true);
                                    setRawLyricInfo(responseBody);
                                    setLastLyricReceieveTimeMS(responseBody.currentTimeMS);
                                    setRoundNumber(responseBody.roundNumber);
                                    setTotalRounds(responseBody.totalRounds);
                                    setMainTextContent(responseBody.lyrics);
                                    setCurrentLyricID(responseBody.lyricID);
                                    let timeDifferenceMS = responseBody.backupTimeMS - TimeRanges.getTime();
                                    setCountdown(Math.round(timeDifferenceMS / 1000)); // should be kinda accurate, seen as it holds on 1 second till the next one comes in would just result in a longer pause on 1. no biggie 
                                    setNeedsToCountdown(true);
                                    setIsGameReady(true);
                                }


                            } if (responseBody.responseMessage.includes("[Close Window]")) {
                                localStorage.setItem('notification', responseBody.notification);
                                window.location.href = "/lyriclabs";
                            }
                            if (responseBody.responseMessage.includes("[CLOSE_WINDOW]")) {
                                localStorage.setItem('notification', responseBody.notification);
                                window.location.href = "/lyriclabs";
                            }
                        }
                    });
                    // heartbeats from server are sent here.
                    client.subscribe(`/topic/heartbeatResponse`, (message) => {
                        if (message.body) {
                            // console.log('<--', message.body);
                            clearTimeout(timeout); // reset timer for 5 more seconds. 
                        }
                    });

                    // Once connected, send a join room message
                    client.publish({
                        destination: "/app/joinGame",
                        body: JSON.stringify({
                            roomID: roomID
                        }),
                    });

                    // send initial heartbeat to prevent user disconnecting if they refresh their page at specific timings.  
                    // console.log("--> Heartbeat");
                    stompClientRef.current.publish({
                        destination: "/app/heartbeat",
                        body: JSON.stringify({
                            heartbeatMessage: "hb"
                        })
                    });
                };

                client.onDisconnect = (frame) => {
                    console.log('Disconnected:', frame);
                    setConnected(false);
                    connectToWebSocket();
                    // navigate('/');
                    // window.location.href = '/';
                };

                const sendHeartbeat = () => {
                    if (stompClientRef.current) {
                        // console.log("--> Heartbeat");
                        stompClientRef.current.publish({
                            destination: "/app/heartbeat",
                            body: JSON.stringify({
                                heartbeatMessage: "hb"
                            })
                        });

                        timeout = setTimeout(() => {
                            console.warn("Did not receive heartbeat from server. Disconnecting...");
                            closeWebSocket();
                        }, 2 * HEARTBEAT_MS + 1000); // accounts for delay in networking and a singular missed heartbeat from the server for unexpected reasons.
                    } else {
                        console.log("not reading stompClient as existing (sendHeartbeat())");
                    }
                };

                client.activate();
                stompClientRef.current = client;
                // sendHeartbeat();
                interval = setInterval(sendHeartbeat, HEARTBEAT_MS);
            }
        };

        connectToWebSocket();

        return () => {
            if (stompClientRef && connectRef.current) {
                closeWebSocket();
            }
        };

    }, [roomID]);

    // Styling

    const styles = {
        artistGridContainer: {
            display: 'flex',
            flexWrap: 'wrap',
            justifyContent: 'center',
            gap: '1rem',
        },
        artistItem: {
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            textAlign: 'center',
        },
        artistImage: {
            width: '100px',
            height: '100px',
            borderRadius: '50%',
            objectFit: 'cover',
        },
        artistName: {
            marginTop: '0.5rem',
        },
    };


    return (
        <Grid container style={{ height: 'auto' }}>

            <Grid container item xs={12} spacing={2} sx={{ height: '95vh' }}>

                {/* <Hidden smDown> */}
                <Grid item xs={12} md={3} style={{ backgroundColor: theme.palette.background.three }}>
                    {/* Left Side: Scoreboard */}

                    <SideCard >
                        <TitleCard title="Scoreboard" style={{ height: '5vh' }}></TitleCard>
                        <Scoreboard users={users} />
                    </SideCard>
                </Grid>
                {/* </Hidden> */}

                {/* Area 2 - Middle Content Area */}
                <Grid item xs={12} md={6} style={{ backgroundColor: theme.palette.background.two, overflow: 'hidden' }}>
                    {/* Center: Game Screen */}
                    <Paper elevation={3} sx={{
                        backgroundColor: theme.palette.background.three,
                        color: theme.palette.text.main,
                        flexGrow: 1, m: 2, p: 2, display: 'flex', flexDirection: 'column',
                        justifyContent: 'space-around',
                        overflow: 'hidden',
                        height: '95vh'
                    }}>

                        <div>
                            <TitleCard style={{ height: '100%' }} title={!isPlaying && isGameReady ? "Game Starting in " : isPlaying ? `Round ${roundNumber} / ${totalRounds}` : mainTextContent}>

                            </TitleCard>
                            <ConnectionTooltip connected={connected}></ConnectionTooltip>
                        </div>

                        <div>
                            {isGameReady ?
                                <Typography label="countdown" variant="h3" align="center" style={{ margin: 10 }}>
                                    {countdown}
                                </Typography>
                                :
                                <Typography label="countdown" variant="h3" align="center" style={{ margin: 10, opacity: 0 }}>
                                    Invisible
                                </Typography>
                            }
                        </div>

                        <Paper sx={{ backgroundColor: theme.palette.background.one, color: theme.palette.text.main }}>
                            {/* Post-Game Analysis Button */}
                            {savingGameData &&
                                <LinearProgress variant="determinate" value={(savingGameCountdown / TIMER_TIME) * 100} />
                            }
                            {isGameReady && !isGameOver &&
                                (countdown > 0
                                    ? <LinearProgress variant="determinate" value={normalise(countdown)} />
                                    : <LinearProgress variant="indeterminate" />)
                            }
                            {/* <Typography sx={{ color: theme.palette.primary.main, m: 2 }}>
                                All Lyrics are provided By Musixmatch.coms public API
                            </Typography> */}

                            {!isGameOver ? (
                                <React.Fragment>
                                    {isPlaying && mainTextContent.split('\n').map((line, index) => ( // so that a new line appears, cause apparently they arent handled directly. 
                                        <Typography key={index} variant="h5" align="center" style={{ margin: '1rem 0' }}>
                                            {line}
                                        </Typography>
                                    ))}
                                    {!isPlaying && (
                                        <Typography label="lyricDisplay" variant="h5" align="center" style={{ margin: '1rem 0' }}>
                                            {"Lyrics Will Display Here"}
                                        </Typography>
                                    )}
                                    {isGameReady && !isGameOver && (
                                        countdown > 0
                                            ? <LinearProgress variant="determinate" value={normalise(countdown)} />
                                            : <LinearProgress variant="indeterminate" />
                                    )}
                                </React.Fragment>
                            ) : (
                                <div style={{ padding: 10 }}>
                                    <Button2 variant="contained" color="primary" disabled={savingGameCountdown > 0} onClick={handlePostGameAnalysis} style={{ padding: 5, height: '3rem' }}>
                                        View Post-Game Analysis
                                    </Button2>
                                </div>
                            )}
                        </Paper>


                        {artistList && artistList.length > 0 && (
                            <Grid
                                sx={{
                                    backgroundColor: canGuess ? theme.palette.background.one : theme.palette.background.three,
                                    border: `3px solid ${canGuess == false ? ((correctGuess && !guessTimeout) ? theme.palette.primary.main : isPlaying ? !guessTimeout ? 'red' : 'transparent' : 'transparent') : 'transparent'}`,
                                    borderRadius: '5px',
                                    justifyContent: 'center',
                                    display: 'flex',
                                    padding: 1,
                                }}
                                container
                                justifyContent="space-evenly"
                                alignItems="center"
                            >
                                {artistList.map((artist, index) => (
                                    <Grid item key={index} xs={12} sm={6} md={4} lg={3} xl={2}>
                                        {/* <div
                                        className={`artist-item ${!canGuess ? (correctGuess ? 'correct-guess' : 'wrong-guess') : ''}`}
                                        onClick={() => isPlaying && canGuess ? handleArtistClick(artist) : null}
                                    > */}
                                        <img
                                            onClick={() => handleArtistClick(artist)}
                                            src={artist.artURL}
                                            alt={artist.artistName}
                                            className="artist-image"
                                            style={{
                                                width: '100px',
                                                height: '100px',
                                                borderRadius: '50%',
                                                objectFit: 'cover',
                                                pointerEvents: canGuess ? 'auto' : 'none',
                                            }}
                                        />
                                        <div className="artist-name">{artist.artistName}</div>
                                        {/* </div> */}
                                    </Grid>
                                ))}
                            </Grid>

                        )}
                        {rawLyricInfo && (
                            <>
                                {rawLyricInfo.hasHtmlTracking && rawLyricInfo.htmlTrackingUrl && (
                                    <div dangerouslySetInnerHTML={{ __html: rawLyricInfo.htmlTrackingUrl }} />
                                )}

                                {rawLyricInfo.hasScriptTracking && rawLyricInfo.scriptTrackingUrl && (
                                    <script src={rawLyricInfo.scriptTrackingUrl}></script>
                                )}

                                {rawLyricInfo.hasPixelTracking && rawLyricInfo.pixelTrackingUrl && (
                                    <img src={rawLyricInfo.pixelTrackingUrl} alt="Tracking Pixel" style={{
                                        width: '1px', height: '1px'
                                        , visibility: 'hidden'
                                    }} />
                                )}
                            </>
                        )}

                        <LegalNotification spotify={true} musixmatch={true}></LegalNotification>
                    </Paper>
                </Grid>

                {/* Area 3 - Side area that hides on small screens */}
                <Hidden smDown>
                    <Grid item xs={12} md={3} style={{ backgroundColor: theme.palette.background.two }}>
                        <SideCard>
                            <TitleCard title="History" style={{ height: '5vh' }}></TitleCard>
                            <DefaultTooltip title={"How did they guess with a lower time after me?"} text={"For fairness and data accuracy, guess times are calculated from the time a user recieves their lyrics, so connection quality can cause people to guess after you with a lower time"}></DefaultTooltip>
                            {historyRecord != null && (
                                <History history={historyRecord} style={{ alignItems: "center" }} />
                            )}
                        </SideCard>
                    </Grid>
                </Hidden>

            </Grid>
        </Grid>
    );

};

export default Game;
