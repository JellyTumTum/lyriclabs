import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Grid, Typography, Container, Card, Paper, LinearProgress } from '@mui/material';
import { Chart, CategoryScale, LinearScale, BarElement, PointElement, Title, Tooltip, ScatterController, Legend, LineElement, ArcElement } from 'chart.js';
import { Bar, Line, Pie, Scatter } from 'react-chartjs-2';
import { useTheme } from './ThemeContext.js';
import TitleCard from './UI Components/TitleCard.js';


Chart.register(CategoryScale, LinearScale, BarElement, PointElement, LineElement, ArcElement, Title, Tooltip, Legend);

const Results = () => {
    const { currentTheme: theme } = useTheme();
    const [loadingMessage, setLoadingMessage] = useState('');
    const [loading, setLoading] = useState(true);
    const [countdown, setCountdown] = useState(10);
    const [percentGuessLineGraphData, setPercentGuessLineGraphData] = useState(null);
    const [roomScatterGraphData, setRoomScatterGraphData] = useState(null);
    const [timeScatterGraphData, setTimeRoomScatterGraphData] = useState(null);
    const [multiplayerPracticeScatterGraph, setMultiplayerPracticeScatterGraph] = useState(null);
    const LOAD_WAIT = 3;

    const fetchData = async () => {
        try {
            let token = localStorage.getItem("jwtToken");
            const headers = {}
            if (token != null) {
                const headers = {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                };
            } else {
                const headers = {}
            }

            const response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/analysis/load`, { headers });

            if (response.data.responseMessage && response.data.responseMessage.includes("[LOADING]")) {
                let counter = LOAD_WAIT * 1000;
                setLoadingMessage(`No Result data has been compiled recently. The data is now being compounded into graphs.  Retrying in ${Math.round(counter / 1000)} seconds.`);
                setLoading(true);

                const interval = setInterval(() => {
                    setCountdown(counter);
                    setLoadingMessage(`No Result data has been compiled recently. The data is now being compounded into graphs. Retrying in ${Math.round(counter / 1000)} seconds.`);
                    counter -= 100;
                    if (counter < 0) {
                        clearInterval(interval);
                        fetchData();
                    }
                }, 100);
            } else {
                setLoadingMessage('');
                setLoading(false);
                setPercentGuessLineGraphData(response.data.percentGuessLineGraphData);
                setRoomScatterGraphData(response.data.roomAverageResponseAccuracyScatterGraph);
                setTimeRoomScatterGraphData(response.data.timeRoomAverageResponseAccuracyScatterGraph);
                setMultiplayerPracticeScatterGraph(response.data.multiplayerPracticeTimeComparisonScatterGraph);
                console.log("Data loaded");
                console.log(response.data);
            }
        } catch (error) {
            console.error("Fetching error:", error);
            setLoadingMessage('');
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    // Chart.js styling --> straight from GameStats.js
    const baseOptions = {
        scales: {
            x: {
                ticks: {
                    color: '#FFFFFF',
                },
                grid: {
                    color: 'rgba(255, 255, 255, 0.1)',
                },
                title: {
                    display: true,
                    text: 'X-axis Label',
                    color: '#FFFFFF',
                },
            },
            y: {
                ticks: {
                    color: '#FFFFFF',
                },
                grid: {
                    color: 'rgba(255, 255, 255, 0.1)',
                },
                title: {
                    display: true,
                    text: 'Y-axis Label',
                    color: '#FFFFFF',
                },
            },
        },
        plugins: {
            legend: {
                labels: {
                    color: '#FFFFFF',
                },
            },
            tooltip: {
                titleColor: '#FFFFFF',
                bodyColor: '#FFFFFF',
                borderColor: 'rgba(255, 255, 255, 0.3)',
                borderWidth: 1,
            },
        },
    };

    const lineGraphOptions = {
        ...baseOptions, // notation to share from baseOptions to this one.
        scales: {
            ...baseOptions.scales,
            y: {
                ...baseOptions.scales.y,
                type: 'linear',
                min: 0,
                max: 100,
                ticks: {
                    ...baseOptions.scales.y.ticks,
                    stepSize: 10,
                },
                title: {
                    ...baseOptions.scales.y.title,
                    display: true,
                    text: '% of Guess Time used For Respective Guess'
                }
            },
            x: {
                ...baseOptions.scales.x,
                title: {
                    ...baseOptions.scales.x.title,
                    display: true,
                    text: 'Max Guess Time',
                },
            },
        },
    };

    const scatterChartOptions = {
        ...baseOptions, // notation to share from baseOptions to this one.
        scales: {
            ...baseOptions.scales,
            y: {
                ...baseOptions.scales.y,
                beginAtZero: true,
                ticks: {
                    ...baseOptions.scales.y.ticks,
                    stepSize: 10, // sets y axis to go up in 10s so its not well squished (default was 50 per step so was 2 blocks high)
                },
                title: {
                    ...baseOptions.scales.y.title,
                    display: true,
                    text: '% of correct first guesses'
                }
            },
            x: {
                ...baseOptions.scales.x,
                title: {
                    ...baseOptions.scales.x.title,
                    display: true,
                    text: 'Response Time',
                },
            },
        },
        plugins: {
            ...baseOptions.plugins,
            legend: {
                display: false,
            },
        },
        onClick: (evt, element, chart) => {
            console.log(element);
            if (element.length > 0) {
                const roomID = roomScatterGraphData.datasets[element[0].datasetIndex].data[0].roomID;
                window.open(`/game-stats/${roomID}`, '_blank');
            }

        }
    }

    const multiplayerPracticeScatterOptions = {
        ...baseOptions, // Spread syntax to share properties from baseOptions to this one.
        scales: {
            ...baseOptions.scales,
            y: {
                ...baseOptions.scales.y,
                beginAtZero: true,
                max: 15, // Sets the maximum value of the y-axis to 15
                ticks: {
                    ...baseOptions.scales.y.ticks,
                    stepSize: 1, // Adjusts y-axis to increment by 1s for finer granularity
                },
                title: {
                    ...baseOptions.scales.y.title,
                    display: true,
                    text: 'Average Correct Guess Time in Practice Games (s)', // Updated label text
                }
            },
            x: {
                ...baseOptions.scales.x,
                beginAtZero: true,
                max: 15, // Sets the maximum value of the x-axis to 15
                ticks: {
                    ...baseOptions.scales.x.ticks,
                    stepSize: 1, // Adjusts x-axis to increment by 1s for finer granularity
                },
                title: {
                    ...baseOptions.scales.x.title,
                    display: true,
                    text: 'Average Correct Guess Time in Multiplayer Games (s)', // Updated label text
                },
            },
        },
        plugins: {
            ...baseOptions.plugins,
            legend: {
                display: false, // Disables the legend display
            },
            annotation: {
                annotations: {
                    lineYEqualsX: {
                        type: 'line',
                        borderColor: 'red',
                        borderWidth: 2,
                        label: {
                            enabled: true,
                            content: 'y = x',
                            position: 'center'
                        },
                        // Chart.js 3 uses xMin, xMax, yMin, yMax for line positioning
                        xMin: 0,
                        xMax: 15,
                        yMin: 0,
                        yMax: 15,
                    }
                }
            }
        },
    };

    const graphDivStyling = {
        margin: 5
    }

    if (loading) {
        return (
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                height: '95vh',
                flexDirection: 'column'
            }}>
                <Typography>{loadingMessage}</Typography>
                <LinearProgress variant="determinate" value={(countdown / 1000) * 100 / LOAD_WAIT} sx={{ width: "75vw", borderRadius: '10px' }} />
            </div>
        )
    }

    return (

        <Grid container spacing={3} sx={{ width: '100vw' }} >
            <Grid item xs={12} sx={{ height: 'auto', margin: 1 }}>
                <TitleCard title="Results (WIP)" style={{ height: '10vh' }}></TitleCard>
                <div style={{ paddingTop: 10 }}>
                    <Typography variant="body">Here you can find graphs that congregate all (depending on the graph, some only include data from specific modes, which you will find stated below each graph) data to attempt to answer different hypothesis about the underlying experiment. These Graphs are stored for 30 minutes, so new data may not appear immediately</Typography>
                </div>
            </Grid>
            <Grid item xs={12} md={12} lg={6}>
                <Paper sx={{ width: '100%', height: '100%', backgroundColor: theme.palette.background.paper, padding: 2, color: theme.palette.text.main }}>
                    <TitleCard title="Time Pressure" style={{ height: '10vh', marginBottom: 10 }}></TitleCard>
                    <div>
                        {percentGuessLineGraphData &&
                            <React.Fragment>
                                <Typography variant="h5">% of Allocated Guess Time used</Typography>
                                <Line data={percentGuessLineGraphData} options={lineGraphOptions} />
                                <Typography variant="body">This Graph Attempts to demonstrate the effect that lowering allowed guess time has on a players speed to guess. It shows 2 % values for possible max guess time, an average % taken to take a first guess, and the average % taken to get the correct answer. This graph is compiled from every Room-Based Game held on the site, and my aim is for it to display a line as close to horizontal as possible, which would imply that lower guess time causes users to act faster (which as a % would appear similar for all guess times)</Typography>
                            </React.Fragment>
                        }
                    </div>
                    <div style={graphDivStyling}>
                        {timeScatterGraphData &&
                            <React.Fragment>
                                <Typography variant="h5">Average Response Time and Accuracy (first guess)</Typography>
                                <Scatter data={timeScatterGraphData} options={scatterChartOptions} />
                                <Typography variant="body">This scatter graph (and a very similar one in the Multiplayer Pressure Section) aims to display a sort of heatMap in graph form. It plots the % of first guesses that are correct, against response Time. Specifically for this version, the color of the plotted points depends on the maxGuessTime setting for that room (each point represents a full room-games average) and hopefully it would show a lower % of accuracy for reduced guess times (lower guess time = hotter).</Typography>
                            </React.Fragment>}
                    </div>
                </Paper>
            </Grid>
            <Grid item xs={12} md={12} lg={6}>
                <Paper sx={{ width: '100%', height: '100%', backgroundColor: theme.palette.background.paper, padding: 2, color: theme.palette.text.main }}>
                    <TitleCard title="Multiplayer Pressure" style={{ height: '10vh', marginBottom: 10 }}></TitleCard>
                    {roomScatterGraphData && <Scatter data={roomScatterGraphData} options={scatterChartOptions} />}
                    <div style={graphDivStyling}>
                        {multiplayerPracticeScatterGraph &&
                            <React.Fragment>
                                <Typography variant="h5">Multiplayer and Practice Correct Guess Response Times </Typography>
                                <Scatter data={multiplayerPracticeScatterGraph} options={multiplayerPracticeScatterOptions} />
                                <Typography variant="body">This graph compares the average correct guess time for each user (dot) on their multiplayer games and their practice games. </Typography>
                            </React.Fragment>}
                    </div>
                </Paper>
            </Grid>
            <Grid item xs={12} md={12} lg={12}>
                {/* <Paper sx={{ width: '100%', height: '100%', backgroundColor: theme.palette.background.paper, padding: 2 }}>
                    <Typography>Section 3 Placeholder</Typography>
                </Paper> */}
            </Grid>
        </Grid >
    );

};

export default Results;
