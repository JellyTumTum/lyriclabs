// GameStatisticsPage.js
import React, { useState, useEffect } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import { Container, Typography, Select, MenuItem, FormControl, InputLabel, Paper, Box, Accordion, Grid, LinearProgress } from '@mui/material';
import { Chart, CategoryScale, LinearScale, BarElement, PointElement, Title, Tooltip, Legend, LineElement, ArcElement, } from 'chart.js';
import { Bar, Line, Pie } from 'react-chartjs-2';
import { useNavigate } from 'react-router-dom';
import { useTheme } from './ThemeContext';
import { Dark, Light } from './Themes.js';
import axios from 'axios';
import TitleCard from './UI Components/TitleCard.js';
import CustomCard from './UI Components/CustomCard.js';
import Button2 from './UI Components/Buttons/Button2.js';
import DefaultTooltip from './UI Components/DefaultTooltip.js';
import GlobalLoader from './UI Components/GlobalLoader.js';
import RoundResultsDisplay from './RoundResultsDisplay.js';
import LegalNotification from './UI Components/LegalNotification.js';
import { CountertopsOutlined } from '@mui/icons-material';
import { Global } from '@emotion/react';

Chart.register(CategoryScale, LinearScale, BarElement, PointElement, LineElement, ArcElement, Title, Tooltip, Legend);

const GameStatistics = () => {

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();
    const { roomID } = useParams(); // This captures the :roomID from the URL
    const location = useLocation();
    const token = localStorage.getItem('jwtToken');

    const [tryCount, setTryCount] = useState(0);
    const [countdown, setCountdown] = useState(5);

    const [loadedGraphs, setLoadedGraphs] = useState(false);

    // InitLoading values.
    const [loadedInit, setLoadedInit] = useState(false);
    const [userList, setUserList] = useState([]);
    const [artistList, setArtistList] = useState([]);
    const [lobbyName, setLobbyName] = useState("");
    const [isPractice, setIsPractice] = useState(true);
    // const [isUserInGame, setIsUserInGame] = useState(false); // can just check if currentUser == null
    const [currentUser, setCurrentUser] = useState(false);

    // GameLoading values
    const [rawGameGraphData, setRawGameGraphData] = useState(null);
    const [loadedGameGraphs, setLoadedGameGraphs] = useState(false);
    const [avgResponseBarChartData, setAvgResponseBarChartData] = useState(null);
    const [correctGuessLineGraphData, setCorrectGuessLineGraphData] = useState(null);
    const [correctGuessPieChartData, setCorrectGuessPieChartData] = useState(null);

    // userSpecific Values
    const [selectedPlayerText, setSelectedPlayerText] = useState('');
    const [chosenUser, setChosenUser] = useState(null);
    const [selectedUserArtistText, setSelectedUserArtistText] = useState('');
    const [userFrontendArtist, setUserFrontendArtist] = useState(null);
    const [loadedUserGraphs, setLoadedUserGraphs] = useState(false);
    const [histogramData, setHistogramData] = useState(null);
    const [userArtistBarChartData, setUserArtistBarChartData] = useState(null);
    const [roundGuessData, setRoundGuessData] = useState(null);

    const [selectedGameArtistText, setSelectedGameArtistText] = useState('');
    const [gameFrontendArtist, setGameFrontendArtist] = useState(null);
    const [usernames, setUsernames] = useState([]); // Populate with usernames fetched from backend

    const prunePracticeData = async () => {
        try {
            // this seems to run twice, but its cached on the other end anyway so not crazy big deal atm. (05/02/24)
            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/gameStats/prunePracticeData`, {
                params: { roomID, frontendArtist: userFrontendArtist, chosenUserID: chosenUser },
                headers: { Authorization: `Bearer ${token}` },
                withCredentials: true
            });

            console.log(response.data);
            setHistogramData(response.data.guessTimeHistogram);
            setUserArtistBarChartData(response.data.userArtistBarChart);
            setLoadedUserGraphs(true);
            // TODO: Handle Error responses through responseMessage, like [CLOSE_WINDOW] and [REFRESH]
        } catch (error) {
            console.error('Error fetching user stats:', error);
            setLoadedUserGraphs(false);
            // TODO: HANDLE

        }
    };

    useEffect(() => {
        const fetchGameStats = async () => {
            try {
                // this seems to run twice, but its cached on the other end anyway so not crazy big deal atm. (05/02/24)
                const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/gameStats/load`, {
                    params: { roomID },
                    headers: { Authorization: `Bearer ${token}` },
                    withCredentials: true
                });

                console.log(response.data);
                setUserList(response.data.userList);
                setIsPractice(response.data.practice);
                setArtistList(response.data.artistList);
                setLobbyName(response.data.lobbyName);
                setCurrentUser(response.data.currentUser);
                if (response.data.currentUser != null) {
                    setSelectedPlayerText(response.data.currentUser.value);
                    setChosenUser(response.data.currentUser.key);
                }
                if (response.data.responseMessage.includes('[INVALID_ROOM_ERROR]')) {
                        localStorage.setItem('notification', response.data.notification);
                        const event = new CustomEvent('notificationSet');
                        window.dispatchEvent(event);
                        navigate('/');

                } else {
                    setLoadedInit(true);
                }
                if (response.data.practice) {
                    setCurrentUser(response.data.userList[0]);
                }

                

                // TODO: Handle Error repsonses through responseMessage, like [CLOSE_WINDOW] and [REFRESH]
            } catch (error) {
                console.error('Error fetching game stats:', error);
                localStorage.setItem('notification', "You are not logged in|To prevent overloading the server with computation, only logged in users can access room-specific statistics.");
                const event = new CustomEvent('notificationSet', {});
                window.dispatchEvent(event);
                setLoadedInit(false);
                navigate("/");
            }
        };
        if (!loadedInit) {
            fetchGameStats();
        }
    }, [loadedInit]);

    const fetchGameGraphs = async () => {
        try {
            // this seems to run twice, but its cached on the other end anyway so not crazy big deal atm. (05/02/24)
            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/gameStats/loadGameGraphs`, {
                params: { roomID },
                headers: { Authorization: `Bearer ${token}` },
                withCredentials: true
            });

            console.log(response.data);
            setAvgResponseBarChartData(response.data.avgResponseBarChartData);
            setCorrectGuessLineGraphData(response.data.correctGuessLineGraphData);
            setCorrectGuessPieChartData(response.data.correctGuessPieChartData);
            setLoadedGraphs(true);
            setLoadedGameGraphs(true);
            // TODO: Handle Error responses through responseMessage, like [CLOSE_WINDOW] and [REFRESH]
        } catch (error) {
            console.error('Error fetching game stats:', error);
            setLoadedInit(false);
            // TODO: HANDLE

        }
    };

    useEffect(() => {
        if (!loadedGameGraphs) {
            console.log(loadedGameGraphs);
            fetchGameGraphs();
        }
    }, [loadedGameGraphs, roomID, token]);

    useEffect(() => {
        const fetchUserGraphs = async () => {
            try {
                // this seems to run twice, but its cached on the other end anyway so not crazy big deal atm. (05/02/24)
                const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/gameStats/getUserSpecificGraphs`, {
                    params: { roomID, frontendArtist: userFrontendArtist, chosenUserID: chosenUser },
                    headers: { Authorization: `Bearer ${token}` },
                    withCredentials: true
                });

                console.log(response.data);
                setHistogramData(response.data.guessTimeHistogram);
                setUserArtistBarChartData(response.data.userArtistBarChart);
                setRoundGuessData(response.data.artistGuessStats);
                setLoadedUserGraphs(true);
                // TODO: Handle Error responses through responseMessage, like [CLOSE_WINDOW] and [REFRESH]
            } catch (error) {
                console.error('Error fetching user stats:', error);
                setLoadedUserGraphs(false);
                // TODO: HANDLE

            }
        };
        if (!loadedUserGraphs && chosenUser != null) {
            fetchUserGraphs();
        }
    }, [loadedUserGraphs, roomID, token, userFrontendArtist, chosenUser]);




    useEffect(() => {
        const updateArtistBarGraph = async () => {
            try {
                console.log("updateArtistBarGraph...")
                // this seems to run twice, but its cached on the other end anyway so not crazy big deal atm. (05/02/24)
                const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/gameStats/updateArtistSpecificGraphs`, {
                    params: { roomID, frontendArtist: gameFrontendArtist },
                    headers: { Authorization: `Bearer ${token}` },
                    withCredentials: true
                });

                console.log(response.data);
                setAvgResponseBarChartData(response.data.barChart);
                setCorrectGuessPieChartData(response.data.pieChart);
                setLoadedGraphs(true);
                // TODO: Handle Error responses through responseMessage, like [CLOSE_WINDOW] and [REFRESH]
            } catch (error) {
                console.error('Error fetching game stats:', error);
                // TODO: HANDLE

            }
        }
        if (gameFrontendArtist != null) {
            updateArtistBarGraph();
        }

    }, [gameFrontendArtist]);


    const handlePlayerSelect = (event) => {
        const selectedValue = event.target.value;
        setSelectedPlayerText(selectedValue);


        const selectedUser = userList.find(user => user.value === selectedValue);
        if (selectedUser) {
            setChosenUser(selectedUser.key);
            setLoadedUserGraphs(false);
            console.log("chosenUser = " + selectedUser.key);
        }
    };


    const handeGameArtistSelect = (event) => {
        setSelectedGameArtistText(event.target.value);
        setGameFrontendArtist(event.target.value);

    }

    const handleUserArtistSelect = (event) => {
        setSelectedUserArtistText(event.target.value);
        setUserFrontendArtist(event.target.value);
        setLoadedUserGraphs(false);
    }

    // Chart.js styling
    const baseOptions = {
        scales: {
            x: {
                ticks: {
                    color: '#FFFFFF', // X-axis labels
                },
                grid: {
                    color: 'rgba(255, 255, 255, 0.1)', // X-axis grid lines
                },
            },
            y: {
                ticks: {
                    color: '#FFFFFF', // Y-axis labels
                },
                grid: {
                    color: 'rgba(255, 255, 255, 0.1)', // Y-axis grid lines
                },
            },
        },
        plugins: {
            legend: {
                labels: {
                    color: '#FFFFFF', // Legend labels
                },
            },
            tooltip: {
                // Tooltip styling
                titleColor: '#FFFFFF',
                bodyColor: '#FFFFFF',
                borderColor: 'rgba(255, 255, 255, 0.3)',
                borderWidth: 1,
            },
        },
    };

    const averageGuessTimingsOptions = {
        ...baseOptions,
        scales: {
            ...baseOptions.scales,
            x: {
                ...baseOptions.scales.x,
                title: {
                    display: true,
                    text: 'Username',
                    color: '#FFFFFF',
                },
            },
            y: {
                ...baseOptions.scales.y,
                title: {
                    display: true,
                    text: 'Average Answer Time',
                    color: '#FFFFFF',
                },
            },
        },
        plugins: {
            ...baseOptions.plugins,
            legend: {
                ...baseOptions.plugins.legend,
                display: false,
            },
            tooltip: {
                ...baseOptions.plugins.tooltip,
            },
        },
    };

    const timeToCorrectGuessOptions = {
        ...baseOptions,
        scales: {
            ...baseOptions.scales,
            x: {
                ...baseOptions.scales.x,
                title: {
                    display: true,
                    text: 'Round Number',
                    color: '#FFFFFF',
                },
            },
            y: {
                ...baseOptions.scales.y,
                title: {
                    display: true,
                    text: 'Response Time',
                    color: '#FFFFFF',
                },
            },
        },
        plugins: {
            ...baseOptions.plugins,
            tooltip: {
                ...baseOptions.plugins.tooltip,
            },
        },
    };

    const avgCorrectAnswerBarOptions = {
        ...baseOptions,
        scales: {
            ...baseOptions.scales,
            x: {
                ...baseOptions.scales.x,
                title: {
                    display: true,
                    text: 'Artist Name',
                    color: '#FFFFFF',
                },
            },
            y: {
                ...baseOptions.scales.y,
                title: {
                    display: true,
                    text: 'Average Answer Time',
                    color: '#FFFFFF',
                },
            },
        },
        plugins: {
            ...baseOptions.plugins,
            legend: {
                ...baseOptions.plugins.legend,
                display: false,
            },
            tooltip: {
                ...baseOptions.plugins.tooltip,
            },
        },
    };

    const responseTimeHistogramOptions = {
        ...baseOptions,
        scales: {
            ...baseOptions.scales,
            x: {
                ...baseOptions.scales.x,
                title: {
                    display: true,
                    text: 'Second Range',
                    color: '#FFFFFF',
                },
            },
            y: {
                ...baseOptions.scales.y,
                title: {
                    display: true,
                    text: 'Total Guesses',
                    color: '#FFFFFF',
                },
            },
        },
        plugins: {
            ...baseOptions.plugins,
            legend: {
                ...baseOptions.plugins.legend,
            },
            tooltip: {
                ...baseOptions.plugins.tooltip,
            },
        },
    };

    const menuItemSx = {
        bgcolor: theme.palette.background.one, color: theme.palette.text.main, '&.Mui-selected, &:hover': {
            backgroundColor: theme.palette.background.one,
            color: theme.palette.primary.main
        }
    }

    const selectItemSx = {
        color: theme.palette.text.main,
        bgcolor: theme.palette.background.three,
        mb: 2,
    }

    const graphTitleSx = {
        margin: 2
    }

    const graphPaperSx = {
        color: theme.palette.background.three
    }

    if (!loadedInit) {
        return (
            <>
                <GlobalLoader></GlobalLoader>
            </>
        );
    }
    else {
        return (
            <Paper sx={{
                bgcolor: theme.palette.background.two, color: theme.palette.text.main, minHeight: '100vh',
                paddingBottom: '20px',
            }}>
                <div style={{ padding: 20 }}>
                    <TitleCard title={lobbyName || "Loading..."}></TitleCard>
                </div>
                <Grid container spacing={2} justifyContent="center" sx={{ padding: 1 }}>
                    {!isPractice && <>
                        <Grid item xs={12} lg={6} >
                            <CustomCard>
                                <Container>
                                    <Typography variant="h3" gutterBottom sx={{ color: theme.palette.text.main, marginBottom: 4.1 }}>
                                        Game Performance
                                    </Typography>
                                    {loadedGameGraphs ? (<>
                                        <Typography variant="h5" sx={graphTitleSx}>
                                            Time to Correct Guess per round
                                        </Typography>

                                        {correctGuessLineGraphData &&
                                            <Line data={correctGuessLineGraphData} options={timeToCorrectGuessOptions} />}
                                        <Typography variant="h6" sx={{ mt: 10 }}>
                                            The Below Graphs are customisable via artist selection
                                        </Typography>
                                        <FormControl fullWidth variant="outlined" sx={{
                                            mt: 3, mb: 2, borderColor: theme.palette.primary.main
                                        }}>
                                            <InputLabel id="artist-select-label" sx={{ color: theme.palette.text.main }}>Select Artist
                                            </InputLabel>
                                            <Select labelId="artist-select-label" id="artist-select" value={selectedGameArtistText}
                                                label="Artist" onChange={handeGameArtistSelect} sx={selectItemSx}>
                                                <MenuItem value="">
                                                    <em>No Artist Filter</em>
                                                </MenuItem>
                                                {artistList.map((artist) => (
                                                    <MenuItem key={artist.spotifyID} value={artist.spotifyID} sx={menuItemSx}>
                                                        {artist.artistName}
                                                    </MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                        <Typography variant="h5" sx={graphTitleSx}>
                                            Average Guess Timings
                                        </Typography>
                                        {avgResponseBarChartData &&
                                            <Bar data={avgResponseBarChartData} options={averageGuessTimingsOptions} />}
                                        {!isPractice && userList[1] != null && correctGuessPieChartData && (
                                            <>
                                                <Typography variant="h5" sx={graphTitleSx}>
                                                    % of players first correct answers
                                                </Typography>
                                                <Pie data={correctGuessPieChartData} options={baseOptions} />
                                            </>
                                        )}</>) : (<GlobalLoader></GlobalLoader>)}

                                </Container>
                            </CustomCard>
                        </Grid>
                    </>}

                    <Grid item xs={12} lg={isPractice ? 12 : 6}>
                        <CustomCard>
                            <Container>
                                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'row', flexWrap: 'wrap' }}>

                                    <Typography variant="h3" gutterBottom sx={{ color: theme.palette.text.main, mr: 2 }}>
                                        Player Performance
                                    </Typography>
                                    {!isPractice ? (<>
                                        <FormControl variant="outlined" sx={{ minWidth: 240, borderColor: theme.palette.primary.main }}>
                                            <InputLabel id="player-select-label" sx={{ color: theme.palette.text.main }}>Select Player</InputLabel>
                                            <Select
                                                labelId="player-select-label"
                                                id="player-select"
                                                value={selectedPlayerText}
                                                label="Player"
                                                onChange={handlePlayerSelect}
                                                sx={selectItemSx}
                                            >
                                                {userList.map((user) => (
                                                    <MenuItem
                                                        key={user.key}
                                                        value={user.value}
                                                        sx={menuItemSx}>
                                                        {user.value}
                                                    </MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                    </>) : (<div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 5 }}>
                                        <Typography variant="h5" sx={{ color: theme.palette.primary.main }}>
                                            {userList[0].value}
                                        </Typography>
                                        <DefaultTooltip title="no player selector?" text="There is no selector required for practice lobbies" placement="top"></DefaultTooltip>
                                    </div>)}
                                </Box>

                                {roundGuessData &&
                                    <React.Fragment>
                                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                            <Typography variant="h5" sx={graphTitleSx}>
                                                General Round Statistics
                                            </Typography>
                                            <DefaultTooltip title="Why dont the round numbers add up?" text="Collaborations count for any artist that's involved (allowing for it to be in multiple of the totals below), and if you do not guess for a round, it will not get included here"></DefaultTooltip>
                                        </div>
                                        <Grid container justifyContent="center">
                                            <Grid item xs={12}></Grid>
                                            <RoundResultsDisplay stats={roundGuessData} />
                                        </Grid>

                                    </React.Fragment>
                                }



                                {isPractice && <><div style={{ marginBottom: 4 }}><Typography variant="h5" sx={graphTitleSx}>
                                    Time to Correct Guess per round
                                </Typography>
                                    {correctGuessLineGraphData &&
                                        <Line data={correctGuessLineGraphData} options={timeToCorrectGuessOptions} />}
                                </div></>}

                                {loadedUserGraphs || (userArtistBarChartData != null && histogramData != null) ? (<>
                                    <Typography variant="h5" sx={{ mb: 2 }}> Average Time For Correct Answer [ Multiplayer | Practice ] </Typography>
                                    {userArtistBarChartData && <Bar data={userArtistBarChartData} options={avgCorrectAnswerBarOptions} />}

                                    <Typography variant="h6" sx={{ mt: 10 }}>
                                        Artist Filter for Response Time Histogram
                                    </Typography>
                                    <FormControl fullWidth variant="outlined" sx={{ mt: 3, mb: 2, borderColor: theme.palette.primary.main }}>
                                        <InputLabel id="user-artist-select-label" sx={{ color: theme.palette.text.main }}>Select Artist</InputLabel>
                                        <Select
                                            labelId="user-artist-select-label"
                                            id="user-artist-select"
                                            value={selectedUserArtistText}
                                            label="Artist"
                                            onChange={handleUserArtistSelect}
                                            sx={selectItemSx}
                                        >
                                            <MenuItem value="">
                                                <em>No Artist Filter</em>
                                            </MenuItem>
                                            {artistList.map((artist) => (
                                                <MenuItem
                                                    key={artist.spotifyID}
                                                    value={artist.spotifyID}
                                                    sx={menuItemSx}
                                                >
                                                    {artist.artistName}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>



                                    {histogramData &&
                                        <React.Fragment>
                                            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                                <Typography variant="h5" sx={graphTitleSx}> Response Times</Typography>
                                                <DefaultTooltip title="why does this graph look wrong?" text="Only correct guesses in multiplayer games are considered here. If viewing this graph from a multiplayer games stats, then the Game-Data is only for this room" placement='top'></DefaultTooltip>
                                            </div>
                                            <Bar data={histogramData} options={responseTimeHistogramOptions} />
                                        </React.Fragment>}

                                    {(currentUser != null) && (currentUser.key == chosenUser) && <>
                                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                            {/* <Button2 variant="contained" color="primary" onClick={prunePracticeData} style={{ margin: 10 }}>
                                                Clear outliars
                                            </Button2>
                                            <DefaultTooltip title="Seeing irregular data?" text="due to the practice-modes unlimited time for each guess, there may be wierd results if you ended up sitting on a practice round, These results should be removed automatically at the end of each practice, but if it was missed, you can try this more agressive clear button." placement="right"></DefaultTooltip> */}
                                        </div>
                                    </>
                                    }
                                </>) :

                                    (<React.Fragment>
                                        <DefaultTooltip title="Why wont it load?" text="You may not of selected a user at the top, it only auto-selects yourself if you were in the room. Or an error has occured. "></DefaultTooltip>
                                        <GlobalLoader></GlobalLoader>

                                    </React.Fragment>)
                                }

                            </Container>
                            <div style={{ marginTop: 10 }}>
                                <LegalNotification spotify={true}></LegalNotification>
                            </div>
                        </CustomCard>

                    </Grid>
                    <div style={{ opacity: 0 }}>
                        <LegalNotification></LegalNotification>
                        {/* literally a glorified spacer  */}
                    </div>

                </Grid>
            </Paper >
        );
    };
}

export default GameStatistics;
