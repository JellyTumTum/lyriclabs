import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Button, List, ListItem, Paper, MenuItem, MenuList, Avatar, IconButton, Typography, TextField, Slider, Switch, Grid, FormControlLabel, Hidden, LinearProgress } from '@mui/material';
import { useTheme } from './ThemeContext.js';
import Scoreboard from './Scoreboard';
import { UpdateRounded } from '@mui/icons-material';
import './Loaders.css';
import { useNavigate } from 'react-router-dom';
import PracticeHistory from './PracticeHistory';
import TitleCard from './UI Components/TitleCard.js';
import SideCard from './UI Components/SideCard.js';
import Button2 from './UI Components/Buttons/Button2.js';
import LyricLoader from './UI Components/LyricCard.js';
import useWarnOnLeave from './WarningFunction.js';
import LegalNotification from './UI Components/LegalNotification.js';
import GlobalLoader from './UI Components/GlobalLoader.js';
import DefaultTooltip from './UI Components/DefaultTooltip.js';

const PracticeGame = () => {
    // removed useWarnOnLeave as it didnt really work anyway
    const { roomID } = useParams();

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();

    const [dontOpenSocket, setDontOpenSocket] = useState(false);

    const stompClientRef = useRef(null); // using ref helps to prevent it being reset of rendering. 
    const connectRef = useRef(null);
    const [connected, setConnected] = useState(false); // dont really know why this is here when connectRef exists but its used in lobby without connectRef being mentioned nearby so its needed. 06/12/23
    const [playerName, setPlayerName] = useState(null);
    const [lobbyName, setLobbyName] = useState(null);

    const [isGameReady, setIsGameReady] = useState(false);
    const [isPlaying, setIsPlaying] = useState(false);
    const [needsToCountdown, setNeedsToCountdown] = useState(false);
    const [isGameOver, setIsGameOver] = useState(false);
    const [displayStartButton, setDisplayStartButton] = useState(false);
    const [waitingForLyrics, setWaitingForLyrics] = useState(false);

    const [roundNumber, setRoundNumber] = useState(0);
    const [totalRounds, setTotalRounds] = useState(0);
    const [lastLyricRecieveTimeMS, setLastLyricReceieveTimeMS] = useState(0);
    const [guessNumber, setGuessNumber] = useState(0);
    const [lastGuessTime, setLastGuessTime] = useState(null);
    const [currentLyricID, setCurrentLyricID] = useState(null);
    const [rawLyricInfo, setRawLyricInfo] = useState(null);
    const [canGuess, setCanGuess] = useState(false);

    // artistDisplay and related variables.
    const [artistList, setArtistList] = useState(null);

    const [users, setUsers] = useState([]);
    const [playerCount, setPlayerCount] = useState(null);
    const [mainTextContent, setMainTextContent] = useState("Waiting for players to connect...");
    const [countdown, setCountdown] = useState(5); // 5 is default wait time value, which is the first countdown that will be used when the game is starting. 

    const [historyRecord, setHistoryRecord] = useState(null);
    const [savingGameData, setSavingGameData] = useState(false);
    const TIMER_TIME = 2000;
    const [savingGameCountdown, setSavingGameCountdown] = useState(TIMER_TIME);

    const [userAnswer, setUserAnswer] = useState("");
    let timeout;
    let interval;

    const closeWebSocket = useCallback(() => { // useCallBack() prevents unessecary rerendering of functions / compoenents or something. 
        if (stompClientRef && connectRef.current) {
            console.log("DEACTIVATING CLIENT");
            stompClientRef.current.deactivate();
            stompClientRef.current = null;
            connectRef.current = null;
            clearInterval(interval); // Cleanup the interval on component unmount
            clearTimeout(timeout); // Cleanup the timeout on component unmount
            console.log("WebSocket closed.");
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
            console.log("reference 1");
            window.location.href = '/';

        }
    }

    // TODO: fix issue with being able to click artist after correct guess
    function handleArtistClick(artist) {
        if (canGuess == true) {
            let currentTime = new Date().getTime();
            console.log("currentTime = " + currentTime);
            console.log("lastLyricRecieveTimeMS = " + lastLyricRecieveTimeMS);
            let guessTimeMS = 0;
            let totalGuessTimeMS = currentTime - lastLyricRecieveTimeMS;
            console.log("totalGuessTimeMS = " + totalGuessTimeMS);
            if ((guessNumber + 1) > 1) {
                guessTimeMS = currentTime - lastGuessTime;
                console.log("(case1) guessTimeMS = " + guessTimeMS);
            } else {
                guessTimeMS = totalGuessTimeMS;
                console.log("(case2) guessTimeMS = " + guessTimeMS);
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
                console.log(guessData);
                console.log("guess should of been sent to backend.")
                setLastGuessTime(currentTime);
                setGuessNumber(guessNumber + 1); // changed at the end to hopefully prevent issues with it not being used
            }
        }
    }

    function handlePostGameAnalysis() {
        setDontOpenSocket(true);
        closeWebSocket();
        // navigate(`/game-stats/${roomID}`);
        window.location.href = `/game-stats/${roomID}`;
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

    const startPractice = () => {
        if (stompClientRef.current && connectRef.current) {
            stompClientRef.current.publish({
                destination: "/app/executeRound",
                body: JSON.stringify({
                    roomID: roomID
                }),
            });
            // setDisplayStartButton(false);
        }
    }

    useEffect(() => {
        if (users == []) {
            console.log("Users is empty");
        }
    }, [users])

    // saving game timer
    useEffect(() => {
        let timerId;

        if (savingGameData && savingGameCountdown > 0) {
            timerId = setTimeout(() => {
                setSavingGameCountdown(savingGameCountdown - 100);
            }, 100);
        } else if (savingGameCountdown === 0) {
            console.log("Unlocked button");
            setMainTextContent("Practice has ended. You can view your stats below");
            setNeedsToCountdown(false);
            setCountdown("");
            setSavingGameData(false);
            setSavingGameCountdown(0);
        }

        return () => clearTimeout(timerId);
    }, [savingGameData, savingGameCountdown]);


    useEffect(() => {
        const connectedUsersCount = users.filter(user => user.isConnected).length;
        if (!isGameReady && !isPlaying) {
            setMainTextContent(`Beginning practice shortly...`);
        }
        if (connectedUsersCount == playerCount && !isPlaying) {
            setMainTextContent("Preperation: Loading Albums...");
        }
    }, [users, playerCount, isGameReady]);

    useEffect(() => {
        if (needsToCountdown) {
            if (countdown > 1) {
                const timerId = setTimeout(() => setCountdown(countdown - 1), 1000);
                return () => clearTimeout(timerId);
            } else {
                setNeedsToCountdown(false);
            }
        }
    }, [countdown, isGameReady]);

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
                                // navigate(`/`);
                                console.log("reference 2");
                                // window.location.href = '/';
                            }
                            if (responseBody.responseMessage.includes("has connected")) {
                                console.log(responseBody.userList);
                                setPlayerCount(responseBody.playerCount);
                                setUsers(responseBody.userList);
                                if (responseBody.responseMessage.includes("[ALL_CONNECTED]" && !isPlaying)) {
                                    setMainTextContent("Preperation: Loading Albums...");
                                }

                                // console.log(users); // --> sadly printing here just prints empty cause line above hasnt taken effect yet. stupid react problems ay.
                            } if (responseBody.responseMessage.includes("[START_GAME_TIMER]")) {
                                setIsGameReady(true); // starts timer using useEffect.
                                setCountdown(5); // technically this is already pre-set but seems like a good approach to set it here to avoid confusion. 
                                setNeedsToCountdown(true);
                            }
                            if (responseBody.responseMessage.includes("force-closed")) {
                                closeWebSocket();
                                localStorage.setItem('notification', 'Force Closure|The lobby you were in seems to of been force-closed. sorry about that');
                                // navigate(`/`);
                                console.log("reference 3");
                                // window.location.href = '/';
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
                                    localStorage.setItem('notification', "Lost Connection|You have unexpectedly lost connection to the lobby");
                                    setConnected(false);
                                }
                            }
                            if (responseBody.responseMessage.includes("[LOADING_RESOURCES]")) {
                                setMainTextContent("Game Preperation: Loading Rescources...");
                            }
                            if (responseBody.responseMessage.includes("[LOADING_ALBUMS]")) {
                                setMainTextContent("Game Preperation: Loading artists albums...");
                            }
                            if (responseBody.responseMessage.includes("[SELECTING_SONGS]")) {
                                setMainTextContent(`Game Preperation: Selecting songs (${responseBody.songNumber}/${responseBody.songCount})...`);
                                if (responseBody.songNumber == responseBody.songCount) {
                                    setDisplayStartButton(true);
                                }
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
                                setDisplayStartButton(false);
                                setWaitingForLyrics(false);
                                // grab and allocate information.
                                setGuessNumber(0); // resets guess count for new round.
                                setCanGuess(true);
                                setRawLyricInfo(responseBody);
                                setLastLyricReceieveTimeMS(new Date().getTime());
                                setRoundNumber(responseBody.roundNumber);
                                setTotalRounds(responseBody.totalRounds);
                                setMainTextContent(responseBody.lyrics);
                                setCurrentLyricID(responseBody.lyricID);
                                // setCountdown(responseBody.maxGuessTime);
                                // setNeedsToCountdown(true);

                            } if (responseBody.responseMessage.includes("[GAME_END]")) {
                                console.log(responseBody.responseMessage);
                                // setIsPlaying(false); --> not really appropriate cause it breaks all the UI condtions. 
                                setMainTextContent("Practice has ended. Saving data...");
                                setNeedsToCountdown(false);
                                setCountdown("");
                                setIsGameOver(true);
                                setSavingGameData(true);

                            }
                            if (responseBody.responseMessage.includes("[ARTIST_LIST_RECIEVED]")) {
                                // idk why I wrote this here, cant even find where I send it on the backend. keeping as a reminder this might be important

                            } if (responseBody.responseMessage.includes("[NEW_HISTORY]")) {

                                console.log(responseBody);
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
                                    console.log("set can guess false");
                                    setWaitingForLyrics(true);
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
                                    setCountdown(0);
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
                                window.location.href = "/";
                            }
                            if (responseBody.responseMessage.includes("[CLOSE_WINDOW]")) {
                                localStorage.setItem('notification', responseBody.notification);
                                window.location.href = "/";
                            }
                        }
                    });
                    // Pretty sure this is never going to be used (26/01/24)
                    // client.subscribe(`/user/queue/timeCheck`, (message) => {
                    //     if (message.body) {
                    //         const timeCheckRequest = JSON.parse(message.body);
                    //         console.log('TimeCheck received:', timeCheckRequest);
                    //         const rtt = Date.now() - timeCheckRequest.clientSendTimeMS;
                    //         const serverToClientTime = Date.now() - timeCheckRequest.recieveTimeMS;

                    //         // Store the serverToClientTime as the latest estimated travel time
                    //         let estimatedTravelTime = serverToClientTime / 2; // assuming symmetric travel times

                    //         // Adjust the guess time using the estimated travel time
                    //         const adjustedGuessTime = timeCheckRequest.clientSendTime + estimatedTravelTime;

                    // }
                    // });
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
                    if (!dontOpenSocket) {
                        connectToWebSocket();
                    }
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
                        }, HEARTBEAT_MS + 1000); // accounts for delay in networking and such. 
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

    if (!connected) {
        return <GlobalLoader></GlobalLoader>;
    } else {
        return (
            <Grid container style={{ width: 'auto', overflowY: 'hidden', overflowX: 'hidden' }}>


                {/* scoreboard would be here but removed for practice */}

                {/* Area 2 - Middle Content Area */}
                <Grid item xs={12} md={9} style={{ backgroundColor: theme.palette.background.two, overflow: 'hidden' }}>
                    {/* Center: Game Screen */}
                    <Paper elevation={3} sx={{
                        backgroundColor: theme.palette.background.three, color: theme.palette.text.main,
                        flexGrow: 1, m: 2, p: 2, display: 'flex', flexDirection: 'column', justifyContent: 'space-around',
                        overflow: 'hidden', height: '95vh'
                    }}>
                        <div>
                            <TitleCard style={{ height: '100%' }} title={!isPlaying && isGameReady ? "Game Starting in " : isPlaying ? `Round ${roundNumber} / ${totalRounds}` : mainTextContent}>
                            </TitleCard>
                        </div>
                        <Hidden mdUp>
                            {historyRecord != null && (
                                <React.Fragment>
                                    <Typography>Last Song Result:</Typography>
                                    <PracticeHistory history={historyRecord} count={1} style={{ alignItems: "center" }} />
                                </React.Fragment>
                            )}
                        </Hidden>

                        <Paper sx={{ backgroundColor: theme.palette.background.one, color: theme.palette.text.main }}>

                            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                {
                                    waitingForLyrics && !isGameOver ? (
                                        <ul className="wave-menu">
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                            <li></li>
                                        </ul>
                                    ) : (
                                        <Typography label="lyricDisplay" variant="h4" align="center" style={{ margin: '1rem 0' }}>
                                            {isPlaying ? mainTextContent : "Lyrics will display here"}
                                        </Typography>
                                    )
                                }
                            </div>

                        </Paper>

                        {/* Post-Game Analysis Button */}
                        {savingGameData &&
                            <LinearProgress variant="determinate" value={(savingGameCountdown / TIMER_TIME) * 100} />
                        }
                        {isGameOver && (
                            <Button2 variant="contained" disabled={savingGameCountdown > 0} onClick={() => handlePostGameAnalysis()}
                                style={{ padding: 5, height: '3rem' }}
                            >
                                View Practice Analysis
                            </Button2>
                        )}

                        {displayStartButton && (
                            <Button2 variant="contained" onClick={startPractice}>
                                Begin Practice
                            </Button2>
                        )}


                        {artistList && artistList.length > 0 && (
                            <Grid sx={{
                                backgroundColor: (!waitingForLyrics || !canGuess) ? theme.palette.background.one : theme.palette.background.two,
                                borderRadius: '5px', justifyContent: 'center', display: 'flex',
                                padding: 1
                            }} container justifyContent="space-evenly" alignItems="center">
                                {artistList.map((artist, index) => (
                                    <Grid item key={index} xs={4} sm={6} md={4} lg={3} xl={2}>
                                        <img src={artist.artURL} onClick={() => handleArtistClick(artist)} alt={artist.artistName} className="artist-image" style={{
                                            width: '7vh', height: '7vh', borderRadius: '50%', objectFit: 'cover', pointerEvents:
                                                canGuess ? 'auto' : 'none',
                                        }} />
                                        <div className="artist-name">{artist.artistName}</div>
                                    </Grid>
                                ))}
                            </Grid>
                        )}

                        <LegalNotification spotify={true} musixmatch={true}></LegalNotification>

                    </Paper>
                </Grid>

                {/* Area 3 - Side area that hides on small screens */}
                {/* <Hidden smDown> */}
                <Grid item xs={12} md={3} style={{ backgroundColor: theme.palette.background.two }}>
                    <SideCard>
                        <TitleCard title="History" style={{ height: '5vh' }}></TitleCard>
                        {historyRecord != null && (
                            <PracticeHistory history={historyRecord} style={{ alignItems: "center" }} />
                        )}

                        {/* legal requirements to display */}
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


                    </SideCard>
                </Grid>
                {/* </Hidden> */}

            </Grid>
        );

    };
}

export default PracticeGame;

// TO GET WORKING:
/* 
detect when user has joined, and send first executeRound to backend. 
recieve the round request, and set to appropriate places. 


extras:
add end practice button. 


*/
