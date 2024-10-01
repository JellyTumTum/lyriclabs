import React, { useEffect, useState, useCallback, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Button, List, ListItem, Popper, MenuList, MenuItem, ClickAwayListener, Paper, Avatar, IconButton, Typography, TextField, Slider, Switch, FormControlLabel, menuItemClasses, Grid } from '@mui/material';
import { styled } from '@mui/material/styles';
import ContentCopyOutlinedIcon from '@mui/icons-material/ContentCopyOutlined';
import { useTheme } from './ThemeContext.js';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCrown } from '@fortawesome/free-solid-svg-icons';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteOutlineIcon from '@mui/icons-material//DeleteOutline';
import { debounce } from 'lodash';
import axios from 'axios';
import Button1 from './UI Components/Buttons/Button1.js';
import SideCard from './UI Components/SideCard.js';
import Button2 from './UI Components/Buttons/Button2.js';
import TitleCard from './UI Components/TitleCard.js';
import { useNavigate } from 'react-router-dom';
import LegalNotification from './UI Components/LegalNotification.js';
import ConnectionTooltip from './UI Components/ConnectionTooltip.js';

import WifiIcon from '@mui/icons-material/Wifi';
import GlobalLoader from './UI Components/GlobalLoader.js';
import DefaultTooltip from './UI Components/DefaultTooltip.js';
import { ConnectedTvRounded } from '@mui/icons-material';


const UserRectangle = ({ name }) => {

    const { currentTheme: theme } = useTheme();
    const avatarUrl = `https://ui-avatars.com/api/?name=${name}&length=3&background=1F1F1F&color=fff`;

    return (
        <Paper style={{ display: 'flex', alignItems: 'center', backgroundColor: theme.palette.background.one, color: theme.palette.text.main, padding: 10, marginBottom: 5, borderRadius: 15 }}>
            <Avatar src={avatarUrl} style={{ margin: 2 }} />
            <Typography variant="body1" style={{ margin: 2 }}>{name}</Typography>
        </Paper>
    );
};

const HostRectangle = ({ name }) => {

    const { currentTheme: theme } = useTheme();
    const avatarUrl = `https://ui-avatars.com/api/?name=${name}&length=3&background=1F1F1F&color=d4af37`;

    return (
        <Paper style={{ display: 'flex', alignItems: 'center', backgroundColor: theme.palette.background.one, color: theme.palette.text.main, padding: 10, marginBottom: 5, borderRadius: 15 }}>
            <Avatar src={avatarUrl} style={{ margin: 2 }} />
            <Typography variant="body1" style={{ margin: 2, marginLeft: 5, marginRight: 5 }}>{name}</Typography>
            <FontAwesomeIcon icon={faCrown} style={{ margin: 2, color: "gold" }} />
        </Paper>
    );
};

const PlayerRectangle = ({ name }) => {

    const { currentTheme: theme } = useTheme();
    const avatarUrl = `https://ui-avatars.com/api/?name=${name}&length=3&background=1F1F1F&color=14823B`;

    return (
        <Paper style={{ display: 'flex', alignItems: 'center', backgroundColor: theme.palette.background.one, color: theme.palette.text.main, padding: 10, marginBottom: 5, borderRadius: 15 }}>
            <Avatar src={avatarUrl} style={{ margin: 2 }} />
            <Typography variant="body1" style={{ margin: 2, marginLeft: 5, marginRight: 5 }}>{name}</Typography>
        </Paper>
    );
};


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
        // Correct way to hide the spinner on number inputs without vendor prefixes
        '& input[type=number]': {
            WebkitAppearance: 'none', // CamelCase for '-webkit-appearance'
            MozAppearance: 'textfield', // CamelCase for '-moz-appearance', though this might not be necessary
            '&::-webkit-outer-spin-button, &::-webkit-inner-spin-button': {
                WebkitAppearance: 'none', // CamelCase for '-webkit-appearance'
                margin: 0,
            },
        },
    },
});


const Lobby = () => {

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();
    const [roomValid, setRoomValid] = useState(true);
    const { roomID } = useParams();
    const [playerName, setPlayerName] = useState(null);
    const [lobbyName, setLobbyName] = useState(null);
    const [isHost, setIsHost] = useState(false);
    const stompClientRef = useRef(null); // using ref helps to prevent some issue with it calling multiple times. 
    const connectRef = useRef(null);
    const [connected, setConnected] = useState(false);
    const [artistName, setArtistName] = useState('');
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
            clearInterval(interval);
            clearTimeout(timeout);
            console.log("WebSocket closed.");
        } else {
            console.log("not recived a response yet, so no need to close webSocket");
        }
    }, []);

    function reconnect() {
        if (stompClientRef.current && connectRef.current) {
            stompClientRef.current.publish({
                destination: "/app/reconnect",
            });
            console.log("sent reconnection message");
        }
    }

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
        } else {
            // seems weird that this button would be useless if the if statement fails, so just putting this here so something happens.
            window.location.href = '/lyriclabs';
        }

    }

    function redirectToGame() {
        console.log("Kinda Implemented");
        let redirect = "lyriclabs/game/" + roomID;
        window.location.href = redirect;
    }

    function sendStartGame() {
        console.log("sending start game signal to backend");
        stompClientRef.current.publish({
            destination: "/app/broadcastGameStart",
        });
    }

    const [users, setUsers] = useState([]);
    const copyToClipboard = () => {
        navigator.clipboard.writeText(window.location.href);
    };

    const handleSearch = async (searchTerm) => {
        try {
            let token = localStorage.getItem("jwtToken");
            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };
            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/room/searchArtists`, {
                params: { artistName },
                headers
            });
            setArtistResults(response.data);
            console.log(response.data);
        } catch (error) {
            console.error("Error fetching artists:", error);
            return [];
        };
    }

    const debouncedSearch = debounce(handleSearch, 500); // at 82wpm, 413 characters per minute, 6.8ps, 147ms passes between characters. hopefully this is still responsive
    useEffect(() => {
        if (artistName) {
            debouncedSearch(artistName);
        }
    }, [artistName]);

    useEffect(() => {
        console.log("Config updated:", config);
    }, [config]);

    useEffect(() => {
        if (artistList != null && users != null) {
            if (Object.keys(artistList).length >= 2 && Object.keys(users).length >= 2) {
                setCanStart(true);
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
                    // Subscribing to public messages
                    client.subscribe(`/topic/roomJoined/` + roomID, (message) => {
                        if (message.body) {
                            const responseBody = JSON.parse(message.body);
                            console.log('Received:', responseBody);
                            // RoomID is not valid, user shouldnt really be here. 
                            if (responseBody.responseMessage.includes("Error : Room does not exist.")) {
                                console.log("Room does not exist, closing connection and redirecting user");
                                setRoomValid(false);
                                localStorage.setItem('notification', 'Invalid Room|That room does not exist');
                                closeWebSocket();
                                // navigate('/');
                                window.location.href = '/lyriclabs';
                            }
                            if (responseBody.responseMessage.includes("has joined the Room")) {
                                const isHost = responseBody.responseMessage.startsWith("[H]");
                                console.log("userList = " + responseBody.userList);
                                setUsers(responseBody.userList);
                            }
                            if (responseBody.responseMessage.includes("force-closed")) {
                                closeWebSocket();
                                localStorage.setItem('notification', 'Room Force Closed|Room has been force closed. Sorry about that');
                                // navigate('/');
                                window.location.href = '/lyriclabs';
                            }
                            if (responseBody.responseMessage.includes("left the lobby")) {
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
                                window.location.href = "lyriclabs/practice/" + roomID;
                            } if (responseBody.responseMessage.includes("[CLOSE_WINDOW]")) {
                                localStorage.setItem('notification', responseBody.notification);
                                window.location.href = "/lyriclabs";
                            } if (responseBody.responseMessage.includes("[Close Window]")) {
                                localStorage.setItem('notification', responseBody.notification);
                                window.location.href = "/lyriclabs";
                            }

                        }
                    });

                    // heartbeats from server are sent here.
                    client.subscribe(`/topic/heartbeatResponse`, (message) => {
                        if (message.body) {
                            console.log('<--', message.body);
                            clearTimeout(timeout); // reset timer for 5 more seconds. 
                        }
                    });

                    // Once connected, send a join room message
                    client.publish({
                        destination: "/app/joinRoom",
                        body: JSON.stringify({
                            roomID: roomID,
                            gameType: "game"
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
        marginRight: 10,
        pointerEvents: isHost ? 'auto' : 'none'  // using isDisabled on the textFields makes them look wonky. so this is alternate apporoach.
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
        '&&:hover': {
            color: 'green',
        },
        '&&:hover .MuiSvgIconRoot': {
            color: 'green',
        },
    }

    const leaveButtonStyle = {
        marginTop: 10,
        margin: 10
    };


    if (!connected && connectRef.current) {
        return <div>
            <Button
                variant="contained"
                onClick={reconnect}
                style={{ backgroundColor: theme.palette.primary.main }}>
                Reconnect
            </Button>
        </div>
    }
    if (!connected) {
        return <GlobalLoader></GlobalLoader>;
    } else if (!roomValid && connected) {
        return <div>This room does not exist</div>; // Display error if room is invalid -> routing should send back to home page anyway.
    } else {
        return (
            <Grid container style={{ height: 'auto' }}>

                <Grid container item xs={12} spacing={2} sx={{ height: '95vh' }}>

                    <Grid item xs={12} md={3} >
                        <SideCard>
                            <div style={{ margin: 20}}>
                                <TitleCard title="Players" style={{ height: '5vh' }}></TitleCard>
                            </div>
                            <List sx={{justifyContent: 'center', justifyItems: 'center', alignContent: 'center', alignItems: 'center'}}>

                                {users.sort((a, b) => b.isHost - a.isHost).map((user) => (
                                    <ListItem key={user.id} sx={{justifyContent: 'center'}}>
                                        {user.isHost ? (
                                            <HostRectangle name={user.id} />
                                        ) : playerName === user.id ? (
                                            <PlayerRectangle name={user.id} />
                                        ) : (
                                            <UserRectangle name={user.id} />
                                        )}
                                    </ListItem>
                                ))}
                            </List>
                        </SideCard>
                    </Grid>

                    <Grid item xs={12} md={9} style={{ backgroundColor: theme.palette.background.one }}>

                        <Paper style={{ flex: 1, backgroundColor: theme.palette.background.two, padding: 20, margin: 10 }}>
                            {/* <Typography style={{ color: theme.palette.text.main }}> {lobbyName} </Typography> */}
                            <div style={{ marginBottom: 15 }}><TitleCard title={lobbyName}></TitleCard></div>
                            <div style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                                {/* icon just for centering button. mad stuff */}
                                <WifiIcon sx={{ opacity: 0 }}></WifiIcon>
                                <div style={{ flexGrow: 1, display: 'flex', justifyContent: 'center' }}>
                                    <Button2 onClick={copyToClipboard}>
                                        <Typography variant='h6' sx={{ color: theme.palette.primary.main }}>Copy link</Typography>
                                    </Button2>
                                </div>
                                <ConnectionTooltip connected={connected}></ConnectionTooltip>

                            </div>


                            <Paper style={{ backgroundColor: theme.palette.background.three, display: 'flex', flexDirection: "column", alignItems: 'center', padding: 10, marginTop: 20, marginBottom: 5, borderRadius: 15, flexWrap: 'wrap' }}>
                                <div style={settingsRowStyle}>
                                    <CustomTextField
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
                                            {isHost ? 'Search above to select artists' : 'Wait for the host to add artists'}
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
                                                    <IconButton sx={{ opacity: isHost ? 1 : 0 }} disabled={isHost ? false : true} onClick={() => handleRemoveArtist(index)}>
                                                        <DeleteOutlineIcon sx={{ color: theme.palette.primary.main }} />
                                                    </IconButton>
                                                </ListItem>
                                            ))
                                        ) : (
                                            <Typography sx={{ margin: 2, display: 'center' }}>Artist List Empty</Typography>
                                        )}
                                    </List>
                                </Paper>

                                <div style={settingsRowStyle}>
                                    <CustomTextField
                                        label="Max Players"
                                        variant="outlined"
                                        type="number"
                                        value={config.maxPlayers}
                                        onChange={(e) => { handleSettingChange("maxPlayers", e.target.value) }}
                                        onBlur={(e) => handleSettingChangeCommit("maxPlayers", e.target.value)}
                                        onKeyDown={(e) => handleKeyPress("maxPlayers", e)}
                                        inputProps={{ min: 2, max: 10 }}  // Enforcing min and max --> doesnt seem to work now ive adjusted to remove the 0, but keeping it here cause why not. 
                                        style={textFieldStyle}
                                    />
                                    <Slider
                                        disabled={isDisabled()}
                                        value={typeof config.maxPlayers === 'number' ? config.maxPlayers : settingConstraints["maxPlayers"].min}
                                        min={2}
                                        max={10}
                                        onChange={(e, value) => { handleSettingChange("maxPlayers", value) }}
                                        onChangeCommitted={(e, value) => { handleSettingChangeCommit("maxPlayers", value) }}
                                        aria-labelledby="Max Players"
                                        valueLabelDisplay="auto"
                                        style={sliderStyle}
                                    />
                                </div>

                                <div style={settingsRowStyle}>
                                    <CustomTextField
                                        label="Max Guess Time"
                                        variant="outlined"
                                        type="number"
                                        value={config.maxGuessTime}
                                        onChange={(e) => { handleSettingChange("maxGuessTime", e.target.value) }}
                                        onBlur={(e) => handleSettingChangeCommit("maxGuessTime", e.target.value)}
                                        onKeyDown={(e) => handleKeyPress("maxGuessTime", e)}
                                        style={textFieldStyle}
                                    />
                                    <Slider
                                        disabled={isDisabled()}
                                        value={config.maxGuessTime}
                                        min={5}
                                        max={15}
                                        onChange={(e, value) => { handleSettingChange("maxGuessTime", e.target.value) }}
                                        onChangeCommitted={(e, value) => { handleSettingChangeCommit("maxGuessTime", value) }}
                                        aria-labelledby="Max Guess Time"
                                        valueLabelDisplay="auto"
                                        style={sliderStyle}
                                    />
                                </div>

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
                                {
                                    isHost ? (
                                        <React.Fragment>
                                            {!canStart && <DefaultTooltip title={""} text={""} opacity={0}></DefaultTooltip>}

                                            <Button2 onClick={sendStartGame} disabled={!canStart} style={leaveButtonStyle}>
                                                Start Game
                                            </Button2>
                                            {!canStart && <DefaultTooltip title={"Why can't I start the game?"} text={"To start you need atleast 2 users and 2 artists. if its a solo experience you're after, try out practice."}></DefaultTooltip>}
                                        </React.Fragment>
                                    ) : (
                                        <Button2 disabled style={leaveButtonStyle}>
                                            Waiting for host to start the game
                                        </Button2>
                                    )
                                }
                            </div>
                            <div style={{ margin: 10 }}>
                                <Button2 onClick={leaveLobby} style={leaveButtonStyle}>
                                    Leave Lobby
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


/*
CURRENT ISSUES:
    FEATURES: 

    NEXT IN LINE:

        - implement removal of artists
        - setup sending of artists to new users when they join, might already happen but I doubt it. (maybe check how settings get sent to them and do it there)    

    ONGOING / FUTURE:
    (H). Incorporate game loop 
    (H). Setup Spotify API. need to look into how it works when I make all the calls but users will also have their spotify linked (for the future)
    (H). Figure out how to get artist search working somewhat efficiently. maybe spotify api could be used through frontend somehow -> or collate list of possible artists + picture and send them over at the start of the game, to then populate the search easily. 
    (M). Overall cleanliness of CSS for Lobby.js
    (M). Fix issue with printing of spotify response. 
    (M). Send difficulty for lyrics over and add a display for it on the frontend. 
    (M). Add reconnection logic to heartbeat. (might not add anything so check before trying just noting it down mid-task)
    (L). Sort out Lobby.js so only one window.location.href is used to redirect and it all routes down to that before closing the window, to avoid potentially missing out some cleanup logic.
    (L). Add Notification window or something to home page so if being directed there a message can be displayed (maybe use local storage and remove it if they close the notification / time elapses)
    (L). Maybe add override to when users create a room so if they have a current room open somehow it closes it. 
            - later down the line maybe add a section in the homepage that shows if you have a room open and provides both a join and close button. 
    (L). Add room name changing. probably need some sort of filter


    BUGS / ERRORS:
    - When loading into a room that doesnt exist (from refreshing a page that had an active room before server restart to reproduce) it doesnt send to home screen just shows loading... and still has active websocket.
    - on saving lobby.js it kicks client to homescreen from a lobby page (may not be important to fix)
    - if a user dcs from their host room (so its still active without anyone in it) and joins a room before the 3 heartbeat miss causes it to close, it stays open as they are sending heartbeats again. seems like a low issue but might have crazy consequences
    - if host loses connection in 2 player room, room seems to close and kick secondary user out, instead of making them host.
    

    TESTS:
    - Test forceclose of rooms (techically should only run if room is empty so)


*/

export default Lobby;
