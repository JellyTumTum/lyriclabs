
import './App.css';
import TopBar from './components/TopBar';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { useTheme } from './components/ThemeContext.js';
import React, { useState, useEffect } from 'react';
import Signup from './components/Signup';
import Home from './components/Home';
import Lobby from './components/Lobby';
import Game from './components/Game';
import Practice from './components/Practice';
import PracticeGame from './components/PracticeGame';
import pageLoadResponse from './api/pageLoadResponse';
import Results from './components/Results.js';
import axios from 'axios';
import GameStatistics from './components/GameStatistics.js';
import Notification from './components/UI Components/Notification.js';
import GlobalLoader from './components/UI Components/GlobalLoader.js';
import QrPage from './components/QrPage.js';


function App() {
    const { currentTheme: theme } = useTheme();
    const [userData, setUserData] = useState(null);
    const [isInitialDataLoaded, setIsInitialDataLoaded] = useState(false);

    const [userLoggedIn, setUserLoggedIn] = useState(false);
    let [count, setCount] = useState(0);

    const [notification, setNotification] = useState({ title: '', body: '' });

    useEffect(() => {
        const loadNotification = () => {
            const notificationStr = localStorage.getItem('notification');
            if (notificationStr) {
                const parts = notificationStr.split('|');
                if (parts.length === 2) {
                    setNotification({ title: parts[0], body: parts[1] });
                }
            }
        };

        window.addEventListener('notificationSet', loadNotification);
        loadNotification();

        // Cleanup
        return () => window.removeEventListener('notificationSet', loadNotification);
    }, []);

    const closeNotification = () => {
        setNotification({ title: '', body: '' });
        localStorage.removeItem('notification');
    };

    function clearLocalStorageTokens() {
        console.log("clearing local storage");
        localStorage.removeItem('jwtToken');
    }

    function logOutUser() {
        clearLocalStorageTokens();
        setUserLoggedIn(false);
        setIsInitialDataLoaded(true);
    }

    useEffect(() => {
        const onPageLoad = async () => {
            let token = localStorage.getItem('jwtToken');
            if (token === null) {
                setUserLoggedIn(false);
                setIsInitialDataLoaded(true);
            } else {
                let response;
                try {
                    response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/user/pageload`, { headers: { Authorization: `Bearer ${token}` } }, { withCredentials: true });
                    // expected outputs : response.data.username holds username and responseMessage = (Y) successful authorization and username is returned
                    // or               : response.data.username holds ""       and responseMessage = (N) Jwt Token invalid, need to re-aquire auth.
                    if (response.data.responseMessage.includes("Y")) {
                        setUserData(new pageLoadResponse(response.data.username, "", response.data.responseMessage));
                        setIsInitialDataLoaded(true);
                        setUserLoggedIn(true);
                        // JWT token is still valid so nothing needs to be done
                    } else {
                        logOutUser();
                        setIsInitialDataLoaded(true);

                    }
                } catch (error) {
                    // 401 error probably, sig-nifying user is not authenticated. 
                    try {
                        response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/noAuth`, null, { withCredentials: true });


                        if (response.data.responseMessage.includes("N")) { // both cases below
                            logOutUser();
                            // NT - session token given isnt in the db, so invalid session -> no login.
                            // NE - session token expired, so invalid session -> no login.

                        }
                        else if (response.data.responseMessage.includes("Y")) { // YR - refresh token had less than 24 hours, need to renew JWT and sessionKey (sessionKey has been done automatically)
                            localStorage.setItem('jwtToken', response.data.newJWT);
                            setUserLoggedIn(true);
                            console.log("userLogged in set to true");
                            response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/user/pageload`, { headers: { Authorization: `Bearer ${response.data.newJWT}` } }, { withCredentials: true });
                            // expected outputs : response.data.username holds username and responseMessage = (Y) successful authorization and username is returned
                            // or               : response.data.username holds ""       and responseMessage = (N) Jwt Token invalid, need to re-aquire auth.
                            if (response.data.responseMessage.includes("Y")) {
                                setUserData(new pageLoadResponse(response.data.username, "", response.data.responseMessage));
                                setIsInitialDataLoaded(true);
                            } else {
                                logOutUser();
                                console.log("unexpected outcome but accounted for : returned N from /user/pageload which shouldnt really happen");
                            }

                        }

                    } catch (error) {
                        console.error('Error fetching user data:', error);
                        logOutUser();
                    }
                }

                // JWT is invalid, need to try and reauth it (a session key is available) [This comment appears to be useless, but incase this breaks it might be the saviour for it all so keeping it here]

            }
        };
        count++;
        if (count === 1) {
            onPageLoad();

        } // workaround to prevent it running pageLoad twice, cause for some reason it does this. 

    }, []);

    useEffect(() => {
        // Perform additional actions when userData changes
        if (userData) {

            if (userData.responseMessage.includes("YN")) {
                localStorage.removeItem('jwtToken');
                localStorage.setItem('jwtToken', userData.token);
            }
            if (userData.responseMessage.includes("Y")) {
                // Token < 18 hours old, token is still valid.
            } if (userData.responseMessage.includes("N")) {
                localStorage.removeItem('jwtToken'); // no-op if it doesnt exist, so can safely use to clear out tokens even if one isnt present.
            }
        }
    }, [userData]);


    return (
        <BrowserRouter>
            <div>
                <TopBar userData={userData} sx={{ backgroundColor: theme.palette.background.default }} />
            </div>
            {notification.title && notification.body && (
                <Notification
                    title={notification.title}
                    body={notification.body}
                    onClick={closeNotification}
                />
            )}
            <div style={{
                backgroundColor: theme.palette.background.default,
                minHeight: '95vh',
                height: '95vh',
                color: 'white',
                display: 'flex',
                flexDirection: 'column',
                margin: 0,
                padding: 0,
                overflowY: 'auto',
            }}>

                <div className="App">
                    {isInitialDataLoaded ? (
                        <Routes>
                            <Route path="/" element={<Home userLoggedIn={userLoggedIn} />} />
                            <Route path="/signup" element={<Signup />} />
                            <Route path="/lobby/:roomID" element={<Lobby />} />
                            <Route path="/practice/:roomID" element={<Practice />} />
                            <Route path="/game/:roomID" element={<Game />} />
                            <Route path="/solo/:roomID" element={<PracticeGame />} />
                            <Route path="/game-stats/:roomID" element={<GameStatistics />} />
                            <Route path="/results" element={<Results />} />
                            <Route path="/qr" element={<QrPage />} />
                        </Routes>
                    ) : (
                        <GlobalLoader></GlobalLoader> // Temp loading status indicator -> a spinning music note or something would be cool. 
                    )}
                </div>
            </div>
        </BrowserRouter>
    );
}

export default App;


/* 

TODO: 

- Fix negative numbers, they are present on practice and game so something to do with time logic. 
- Still disconnect during live games but not during lobbies. need to figure out why.

*/