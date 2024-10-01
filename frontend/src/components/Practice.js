import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Button, List, ListItem, Popper, MenuList, MenuItem, ClickAwayListener, Paper, Avatar, IconButton, Typography, TextField, Slider, Switch, FormControlLabel, menuItemClasses, Grid } from '@mui/material';
import { styled } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import { useTheme } from './ThemeContext.js';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCrown } from '@fortawesome/free-solid-svg-icons';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteOutlineIcon from '@mui/icons-material//DeleteOutline';
import { debounce } from 'lodash';
import axios from 'axios';
import DefaultTooltip from './UI Components/DefaultTooltip.js';
import Button2 from './UI Components/Buttons/Button2.js';
import TitleCard from './UI Components/TitleCard.js';
import LegalNotification from './UI Components/LegalNotification.js';
import GlobalLoader from './UI Components/GlobalLoader.js';

const CustomTextField = styled(TextField)({
    '& label.Mui-focused': {
        color: '#1DB954',
    },
    '& label': {
        color: 'white',
    },
    '& .MuiOutlinedInput-root': {
        '& fieldset': {
            borderColor: 'white',
        },
        '&:hover fieldset': {
            borderColor: '#1DB954',
        },
        '&.Mui-focused fieldset': {
            borderColor: '#1DB954',
        },
        '& input': {
            color: 'white',
        },
        '& input[type=number]': {
            WebkitAppearance: 'none',
            MozAppearance: 'textfield',
            '&::-webkit-outer-spin-button, &::-webkit-inner-spin-button': {
                WebkitAppearance: 'none',
                margin: 0,
            },
        },
    },
});

const Practice = () => {

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();
    const [roomValid, setRoomValid] = useState(true);
    const { roomID } = useParams();
    const [playerName, setPlayerName] = useState(null);
    const [lobbyName, setLobbyName] = useState(null);
    const [isHost, setIsHost] = useState(false);
    const stompClientRef = useRef(null); // using ref helps to prevent it being reset of rendering. 
    const connectRef = useRef(null);
    const [connected, setConnected] = useState(false);

    const [artistName, setArtistName] = useState(''); // State to hold the artist name to be added
    const [artistResults, setArtistResults] = useState(null);
    const [validArtist, setValidArtist] = useState(false);
    const [selectedArtist, setSelectedArtist] = useState(null);
    const [artistList, setArtistList] = useState(null);
    const [anchorEl, setAnchorEl] = useState(null);

    const [canStart, setCanStart] = useState(false);

    const [config, setConfig] = useState({
        maxPlayers: 10,
        artistCount: 0,
        artistList: [""],
        usingSongName: false,
        stationName: "",
        usingStation: false,
        gamemode: "classic",
        maxGuessTime: 10,
        waitTime: 3,
        maxSongs: 20,
        minSongs: 10,
        songCount: 0
    });
    const [editing, setEditing] = useState({
        maxPlayers: false,
        artistCount: false,
        artistList: false,
        usingSongName: false,
        stationName: false,
        usingStation: false,
        gamemode: false,
        maxGuessTime: false,
        waitTime: false,
        maxSongs: false,
        minSongs: false,
        songCount: false
    });
    const settingConstraints = {
        maxPlayers: { min: 2, max: 10 },
        artistCount: { min: 2, max: 20 },
        maxGuessTime: { min: 5, max: 15 },
        waitTime: { min: 2, max: 15 },
        maxSongs: { min: config.minSongs, max: 30 },
        minSongs: { min: 5, max: config.maxSongs },
    };

    const [tempConfig, setTempConfig] = useState(null);

    let timeout;
    let interval;
    const isDisabled = () => !isHost;

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

    function leavePractice() {
        console.log("Leaving Practice");
        if (stompClientRef.current && connectRef.current) {
            stompClientRef.current.publish({
                destination: "/app/disconnect",
            });
            console.log("sent disconnection message to backend");
            closeWebSocket();
            // navigate('/');
            window.location.href = '/';

        }
    }

    function redirectToGame() {
        console.log("Kinda Implemented");
        let redirect = "/solo/" + roomID;
        window.location.href = redirect; // need to use over navigate to make websocket logic function. 
    }

    function sendStartPractice() {
        console.log("sending start game signal to backend");
        stompClientRef.current.publish({
            destination: "/app/broadcastPracticeStart",
            // body: JSON.stringify({
            //     roomID: roomID // backend currently doesnt require this, but just incase it becomes used.
            // })
        });
    }

    const [users, setUsers] = useState([]);

    const handleSearch = async (searchTerm) => {
        try {
            let token = localStorage.getItem("jwtToken");
            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json' // removes stupid content_type error
            };
            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/room/searchArtists`, {
                params: { artistName },
                headers
            });
            setArtistResults(response.data);
            console.log(response.data);
        } catch (error) {
            console.error("Error fetching artists:", error);
            return []; // Return an empty array in case of error
        };
    }

    const debouncedSearch = debounce(handleSearch, 500); // at 82wpm, 413 characters per minute, 6.8ps, 147ms passes between characters. hopefully this is still responsive
    useEffect(() => {
        if (artistName) {
            debouncedSearch(artistName);
        }
    }, [artistName]);


    useEffect(() => {
        if (artistList != null) {
            if (Object.keys(artistList).length >= 2) {
                setCanStart(true);
            } else {
                setCanStart(false);
            }
        } else {
            setCanStart(false);
        }

    }, [artistList, users]);

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
                    client.subscribe(`/topic/roomJoined/` + roomID, (message) => {
                        if (message.body) {
                            const responseBody = JSON.parse(message.body);
                            console.log('Received:', responseBody);
                            // RoomID is not valid, user shouldnt really be here. 
                            if (responseBody.responseMessage.includes("Error : Room does not exist.")) {
                                console.log("Room does not exist, closing connection and redirecting user");
                                setRoomValid(false);
                                closeWebSocket();
                                // navigate('/');
                                window.location.href = '/';

                            }
                            if (responseBody.responseMessage.includes("has joined the Room")) {
                                const isHost = responseBody.responseMessage.startsWith("[H]");
                                console.log("userList = " + responseBody.userList);
                                setUsers(responseBody.userList);
                            }
                            if (responseBody.responseMessage.includes("force-closed")) {
                                closeWebSocket();
                                // navigate('/');
                                window.location.href = '/';
                            }
                            if (responseBody.responseMessage.includes("left the lobby")) {
                                setUsers(responseBody.currentPlayers);
                                if (responseBody.responseMessage.includes(playerName)) {
                                    // user has probably gone idle for too long (only known cause of this happening 22/09/23)
                                    // needs to be intended behaviour to prevent unknown interactions --> change if new approach is thought of.
                                    console.log("triggering leaveLobby via joinRoomed left the lobby if statement (29/09/23)");
                                    localStorage.setItem('notification', 'Idle Activity|You were removed from the room due to being idle');
                                    leavePractice();
                                }
                            }
                            if (responseBody.responseMessage.includes("lost connection")) {
                                // handle things accordingly like greying out userRectangle. 
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
                            if (responseBody.responseMessage.includes("Setting Change -")) {
                                setConfig(prevConfig => ({
                                    ...prevConfig,
                                    [responseBody.settingName]: responseBody.settingValue,
                                }));
                            }
                            if (responseBody.responseMessage.includes("[ADD_ARTIST]") || responseBody.responseMessage.includes("[REMOVE_ARTIST]")) {
                                // can tell this was added after Game.js was started due to the responseMessage including the CAPS_ style. just funny (21/12/23)
                                console.log(responseBody.responseMessage);
                                setArtistList(responseBody.artistList); // doesnt need complex prevList => notation as the list contains all the artists anyway.
                            }
                            if (responseBody.responseMessage.includes("[Game Starting]")) {
                                redirectToGame();
                            }
                        }
                    });

                    // Subscribing to private messages for the specific client. 
                    client.subscribe(`/user/queue/privateMessage`, (message) => {
                        if (message.body) {
                            const responseBody = JSON.parse(message.body);
                            console.log('Private message received:', responseBody);
                            if (responseBody.responseMessage === "Joined Room.") {
                                connectRef.current = true;
                                setConnected(true);
                                setConfig(responseBody.config);
                                setLobbyName(responseBody.roomName);
                                setPlayerName(responseBody.username);
                                setUsers(responseBody.userList);
                                setArtistList(responseBody.artistList);
                            }
                            if (responseBody.responseMessage.includes("[Config]")) {
                                if (responseBody.responseMessage.includes("Error")) {
                                    setConfig(prevConfig => ({
                                        ...prevConfig,
                                        [responseBody.settingName]: responseBody.settingValue,
                                    }));
                                }
                            } if (responseBody.responseMessage.includes("[SWITCH_MODE]")) {
                                localStorage.setItem('notification', responseBody.notification);
                                window.location.href = "/lobby/" + roomID;
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

                    // heartbeats from server are sent here.
                    client.subscribe(`/topic/heartbeatResponse`, (message) => {
                        if (message.body) {
                            // console.log('<--', message.body);
                            clearTimeout(timeout); // reset timer for 5 more seconds. 
                        }
                    });

                    // Once connected, send a join room message
                    client.publish({
                        destination: "/app/joinRoom",
                        body: JSON.stringify({
                            roomID: roomID,
                            gameType: "practice"
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

        // Cleanup on component unmount
        return () => {
            if (stompClientRef && connectRef.current) {
                closeWebSocket();
            }
        };

    }, [roomID]);

    useEffect(() => {
        // setIsHost(users.find(u => u.id === playerName).isHost === true); // really proud I made this one liner up so its staying in this comment.
        // sadly the case where the user is not found isnt covered so, this chunky boy is here instead. 
        const user = users.find(u => u.id === playerName);
        if (user) {
            setIsHost(user.isHost);
        } else {
            setIsHost(false);
        }
    }, [users, playerName]);

    // Function to handle adding a new artist
    const handleAddArtist = (artistObject) => {
        if (stompClientRef.current && stompClientRef.current.connected) {
            console.log(artistObject);
            stompClientRef.current.publish({
                destination: "/app/addArtist",
                headers: { "content-type": "application/json" },  // May not be necessary, could also cause an error.
                body: JSON.stringify({
                    roomID: roomID,
                    artist: artistObject,
                })
            });
            setArtistName("");
            setArtistResults(null);
        };
    }

    // Function to handle removing an artist
    const handleRemoveArtist = (index) => {
        if (stompClientRef.current && stompClientRef.current.connected) {
            console.log(artistList[index]);
            stompClientRef.current.publish({
                destination: "/app/removeArtist",
                headers: { "content-type": "application/json" },  // May not be necessary, could also cause an error.
                body: JSON.stringify({
                    roomID: roomID,
                    artist: artistList[index],
                })
            });
        };
    };

    const handleSettingChange = (settingName, value) => {
        // const clampedValue = (settingName === 'maxPlayers') ? 
        //     Math.min(Math.max(value, 2), 10) : value;
        // scared to remove the above code incase this function breaks so it stays.

        setConfig(prevConfig => ({
            ...prevConfig,
            [settingName]: value,
        }));
    };

    const handleSettingChangeCommit = (settingName, value) => {
        if (settingConstraints[settingName]) {
            if (value >= settingConstraints[settingName].min && value <= settingConstraints[settingName].max) {
                // value they had in the box is valid. 
                try {
                    // Check if the WebSocket connection is active
                    if (stompClientRef.current && stompClientRef.current.connected) {
                        stompClientRef.current.publish({
                            destination: "/app/changeSetting",
                            headers: { "content-type": "application/json" },  // May not be necessary, could also cause an error.
                            body: JSON.stringify({
                                roomID: roomID,
                                settingName: settingName,
                                settingValue: value
                            })
                        });
                    } else {
                        console.error("WebSocket is not connected. (settingChange)");
                    }
                } catch (error) {
                    console.error("Failed to send message (settingChange): ", error);
                    // Handle the error - maybe inform the user
                }
            } else {
                // out of range
                const clampedValue = Math.max(settingConstraints[settingName].min, Math.min(value, settingConstraints[settingName].max));
                setConfig(prevConfig => ({
                    ...prevConfig,
                    [settingName]: clampedValue,
                }));
                handleSettingChangeCommit(settingName, clampedValue);
            }
        }
        setEditing(prevEditing => ({
            ...prevEditing,
            [settingName]: false
        }));
    }

    const handleKeyPress = (settingName, e) => {
        // Hopefully this will act as a 'first time edit since last update' sort of deal and save the current state for if the new value entered is not valid. cant wait to see if it works (04/10/23)
        if (!editing[settingName]) {
            setTempConfig(config[settingName]);
            setEditing(prevEditing => ({
                ...prevEditing,
                [settingName]: true
            }));
        }
        if (e.key === 'Enter' || e.key === 'Tab') {
            // Handle submission to backend here
            handleSettingChangeCommit(settingName, e.target.value);
        }
    };


    const styles = {
        playerListContainer: {
            backgroundColor: theme.palette.background.one,
            width: '100%',
            overflowY: 'auto',
            margin: 5,
            boxShadow: '5px 5px 15px rgba(0, 0, 0, 0.3)',  // Shadow for 3D effect
            border: '1px solid rgba(0, 0, 0, 0.2)',       // A slight border
            // borderRadius: '5px',                          // Round the edges a bit for aesthetics
            transition: 'all 0.3s ease',                  // Smooth any state changes
            padding: '10px',                              // Some padding inside

        },
    };

    const settingsRowStyle = {
        display: 'flex',
        alignItems: 'center',
        marginBottom: 10,
        width: '100%'
    };

    const textFieldStyle = {
        backgroundColor: theme.palette.background.two,
        color: theme.palette.text.main,
        flex: 1,
        marginRight: 10
    };

    const iconButtonStyle = {
        marginLeft: 10
    };

    const sliderStyle = {
        // flex: 1,
        marginRight: 100,
        width: 'calc(30%)'
    };

    const listContainerStyle = {
        color: theme.palette.text.main,
        backgroundColor: theme.palette.background.three,
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        width: '100%'
    };

    const menuItemStyle = {
        color: theme.palette.text.main,
        backgroundColor: theme.palette.background.three,
        cursor: 'pointer',
        '&:hover': {
            color: theme.palette.primary.main,
        },
        '&:hover .MuiSvgIcon-root': {
            color: theme.palette.primary.main,
        },
    };

    const leaveButtonStyle = {
        marginTop: 10,
        margin: 10
    };


    // if (!connected && connectRef.current) { --> Pretty sure this code is never accessed, especially not in practice. 
    //     return <div>
    //         <Button
    //             variant="contained"
    //             onClick={reconnect}
    //             style={{ backgroundColor: theme.palette.primary.main }}>
    //             Reconnect
    //         </Button>
    //     </div>
    // }
    if (!connected) {
        return <GlobalLoader></GlobalLoader>;
    } else {
        return (
            <Grid container style={{ height: '95vh' }}>

                <Grid container item xs={12} spacing={2}>
                    {/* Area 3 - (Rest of them arent needed for practice) */}
                    <Grid item xs={12} md={12} style={{ backgroundColor: theme.palette.background.one }}>

                        <Paper style={{ flex: 1, backgroundColor: theme.palette.background.two, padding: 20, margin: 10 }}>

                            <div style={{ marginBottom: 15 }}><TitleCard title={lobbyName}></TitleCard></div>

                            <Paper style={{ backgroundColor: theme.palette.background.three, display: 'flex', flexDirection: "column", alignItems: 'center', padding: 10, marginTop: 20, marginBottom: 5, borderRadius: 15, flexWrap: 'wrap' }}>
                                <div style={settingsRowStyle}>
                                    <CustomTextField
                                        disabled={isDisabled()}
                                        label="Add Artist"
                                        variant="outlined"
                                        value={artistName}
                                        onChange={(e) => setArtistName(e.target.value)}
                                        onKeyDown={(e) => {
                                            if (e.key === 'Enter' && artistResults && artistResults.length > 0) {
                                                handleAddArtist(artistResults[0]);
                                            }
                                        }}
                                        style={textFieldStyle}
                                        sx={{ marginTop: 5 }}
                                    />

                                </div>
                                <MenuList >
                                    {artistResults && artistResults.length > 0 ? (
                                        artistResults.map((artist, index) => (
                                            <MenuItem
                                                key={index}
                                                style={menuItemStyle}
                                                onClick={() => {
                                                    handleAddArtist(artist);
                                                }}
                                            >
                                                <img
                                                    src={artist.artURL}
                                                    alt={artist.artistName}
                                                    style={{ width: '50px', height: '50px', marginRight: '10px' }}
                                                />
                                                {artist.artistName}
                                                <IconButton disabled={true} onClick={() => handleAddArtist(artist)}>
                                                    <AddCircleOutlineIcon sx={{ color: theme.palette.primary.main }} />
                                                </IconButton>
                                            </MenuItem>
                                        ))
                                    ) : (
                                        <Typography sx={{ color: theme.palette.text.main }}>
                                            {isHost ? 'Search above to select artists' : 'Host controls artist selection'}
                                        </Typography>
                                    )}
                                </MenuList>
                                <Paper style={{ width: '100%', color: theme.palette.text.main, backgroundColor: theme.palette.background.three, padding: 10, marginTop: 20, marginBottom: 15, borderRadius: 15, flexWrap: 'wrap' }}>
                                    Current Artists:
                                    <List style={listContainerStyle}>
                                        {artistList && artistList.length > 0 ? (
                                            artistList.map((artist, index) => (
                                                <ListItem key={index} style={listContainerStyle}>
                                                    <img
                                                        src={artist.artURL}
                                                        alt={artist.artistName}
                                                        style={{ width: '50px', height: '50px', marginRight: '10px' }}
                                                    />
                                                    {artist.artistName}
                                                    {isHost && (
                                                        <IconButton onClick={() => handleRemoveArtist(index)}>
                                                            <DeleteOutlineIcon sx={{ color: theme.palette.primary.main }} />
                                                        </IconButton>
                                                    )}
                                                </ListItem>
                                            ))
                                        ) : (
                                            <Typography sx={{ margin: 2, display: 'center' }}>Artist List Empty</Typography>
                                        )}
                                    </List>
                                </Paper>

                                <div style={settingsRowStyle}>
                                    <CustomTextField
                                        label="Min Songs"
                                        variant="outlined"
                                        type="number"
                                        value={config.minSongs}
                                        onChange={(e) => { handleSettingChange("minSongs", e.target.value) }}
                                        onBlur={(e) => handleSettingChangeCommit("minSongs", e.target.value)}
                                        onKeyDown={(e) => handleKeyPress("minSongs", e)}
                                        style={textFieldStyle}
                                    />
                                    <Slider
                                        disabled={isDisabled()}
                                        value={config.minSongs}
                                        min={5}
                                        max={config.maxSongs}
                                        onChange={(e, value) => { handleSettingChange("minSongs", e.target.value) }}
                                        onChangeCommitted={(e, value) => { handleSettingChangeCommit("minSongs", value) }}
                                        aria-labelledby="Min Songs"
                                        valueLabelDisplay="auto"
                                        style={sliderStyle}
                                    />

                                </div>

                                <div style={settingsRowStyle}>
                                    <CustomTextField
                                        label="Max Songs"
                                        variant="outlined"
                                        type="number"
                                        value={config.maxSongs}
                                        onChange={(e) => { handleSettingChange("maxSongs", e.target.value) }}
                                        onBlur={(e) => handleSettingChangeCommit("maxSongs", e.target.value)}
                                        onKeyDown={(e) => handleKeyPress("maxSongs", e)}
                                        style={textFieldStyle}
                                    />
                                    <Slider
                                        disabled={isDisabled()}
                                        value={config.maxSongs}
                                        min={config.minSongs}
                                        max={30}
                                        onChange={(e, value) => { handleSettingChange("maxSongs", e.target.value) }}
                                        onChangeCommitted={(e, value) => { handleSettingChangeCommit("maxSongs", value) }}
                                        aria-labelledby="Max Songs"
                                        valueLabelDisplay="auto"
                                        style={sliderStyle}
                                    />

                                </div>
                            </Paper>
                            <div style={{ margin: 10 }}>
                                <React.Fragment>
                                    {!canStart && <DefaultTooltip title={""} text={""} opacity={0}></DefaultTooltip>}

                                    <Button2 onClick={sendStartPractice} disabled={!canStart} style={leaveButtonStyle}>
                                        Start Practice
                                    </Button2>
                                    {!canStart && <DefaultTooltip title={"Why can't I start?"} text={"To start you need atleast 2 artists. Not really practice if you just have one possible answer. its just clicking a button. "}></DefaultTooltip>}
                                </React.Fragment>
                            </div>
                            <div style={{ margin: 10 }}>
                                <Button2 onClick={leavePractice} style={leaveButtonStyle}>
                                    Leave Practice
                                </Button2>
                            </div>

                            <LegalNotification spotify={true}></LegalNotification>


                        </Paper>
                    </Grid>

                </Grid>

            </Grid>
        );


    };
}

export default Practice;
