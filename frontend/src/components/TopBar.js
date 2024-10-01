import React, { useState, useEffect, useRef } from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import LoginPanel from './LoginPanel'; // Adjust the import path accordingly
import LogoutDropdown from './LogoutDropdown';
import axios from 'axios';
import { useTheme } from './ThemeContext';
import { useNavigate } from 'react-router-dom';
import Button2 from './UI Components/Buttons/Button2';
import './UI Components/TopBar.css';
import LoginRoundedIcon from '@mui/icons-material/LoginRounded';
import ScienceTwoToneIcon from '@mui/icons-material/ScienceTwoTone';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import { Typography } from '@mui/material';

const TopBar = (props) => {

    const navigate = useNavigate();
    const [showLoginPanel, setShowLoginPanel] = useState(false);
    const [showProfileDropdown, setShowProfileDropdown] = useState(false); // State for profile dropdown
    const loginPanelRef = useRef(null);
    const profileDropdownRef = useRef(null);

    const { currentTheme: theme } = useTheme();
    const toggleLoginPanel = (event) => {
        event.stopPropagation(); // prevents double clicking
        console.log('showLoginPanel = ' + showLoginPanel);
        setShowLoginPanel(!showLoginPanel);
        console.log("toggle loginPanel");
    };

    const goSignup = () => {
        setShowLoginPanel(!showLoginPanel);
        console.log('running goSignup');
        navigate(`/signup`);
    }

    const toggleProfileDropdown = (event) => {
        event.stopPropagation(); // prevents double clicking
        setShowProfileDropdown(!showProfileDropdown);
    };

    const goHome = () => {
        // navigate('/');
        window.location.href = '/';
    }

    const handleLogout = async () => {
        // Perform logout action (e.g., clear token and user data)
        console.log("handleLogoutClick");
        let token = localStorage.getItem("jwtToken");
        try {
            let response = await axios.get(`${process.env.REACT_APP_SERVER_URL}/user/logout`, {
                headers: { Authorization: `Bearer ${token}` },
                withCredentials: true
            });
            console.log("response:" + response.data);
        } catch (error) {
            console.error('Error logging out user: ', error);
        }
        localStorage.removeItem("jwtToken");
        window.location.href = '/';

    }

    const handleDocumentClick = (event) => {
        if (showLoginPanel && !loginPanelRef.current.contains(event.target)) {
            toggleLoginPanel(event);
        }
        if (showProfileDropdown && !profileDropdownRef.current.contains(event.target) && !event.target.classList.contains('hoverableIcon')) {
            toggleProfileDropdown();
        };
    }

    useEffect(() => {
        document.addEventListener('mousedown', handleDocumentClick);
        return () => {
            document.removeEventListener('mousedown', handleDocumentClick);
        };
    }, [showLoginPanel]);

    useEffect(() => {
        document.addEventListener('mousedown', handleDocumentClick);
        return () => {
            document.removeEventListener('mousedown', handleDocumentClick);
        };
    }, [showProfileDropdown]);

    return (
        <div style={{ backgroundColor: theme.palette.background.default }}>
            {/* <div style={{
                width: '100vw',
                height: '2vh',
                color: theme.palette.text.main, 
                backgroundColor: theme.palette.error.main, 
                display: 'flex',
                alignItems: 'center', 
                justifyContent: 'center'
            }}>
                <Typography variant="body" sx={{fontSize: '12px'}}>Under Development: Bugs and Issues expected</Typography>
            </div> */}
            <AppBar position="sticky">
                <Toolbar sx={{ backgroundColor: theme.palette.background.one, justifyContent: 'center', height: '5vh' }}>
                    <ScienceTwoToneIcon onClick={goHome} className="hoverableIcon"></ScienceTwoToneIcon>

                    <div className="spinner" style={{ position: 'absolute', left: '50%', transform: 'translateX(-50%)' }}>
                        <span>L</span>
                        <span>Y</span>
                        <span>R</span>
                        <span>I</span>
                        <span>C</span>
                        <span>L</span>
                        <span>A</span>
                        <span>B</span>
                        <span>S</span>
                    </div>

                    <div style={{ flex: 1, display: 'flex', justifyContent: 'flex-end' }}>
                        {props.userData && props.userData.username ? (
                            <div style={{ position: 'relative' }}>
                                <LogoutRoundedIcon onClick={(e) => toggleProfileDropdown(e)}
                                    className="hoverableIcon"></LogoutRoundedIcon>
                                {showProfileDropdown && <LogoutDropdown handleLogout={handleLogout} userData={props.userData} profileDropdownRef={profileDropdownRef} setShowProfileDropdown={setShowProfileDropdown} />}
                            </div>
                        ) : !showLoginPanel ? (
                            <LoginRoundedIcon className="hoverableIcon" onClick={(e) => toggleLoginPanel(e)} color="inherit" />
                        ) : (
                            <div></div> // removed button when the panels open, causes too many problems. added this empty div instead of refactoring this conditional stuff. 
                        )}
                    </div>
                </Toolbar>
                {showLoginPanel && <LoginPanel loginPanelRef={loginPanelRef} setShowLoginPanel={setShowLoginPanel} />}
            </AppBar>


        </div>

    );
}


export default TopBar;
