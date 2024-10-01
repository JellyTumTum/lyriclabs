import React, { useState, useEffect, useRef } from 'react';
import Button from '@mui/material/Button';
import Button2 from './UI Components/Buttons/Button2.js';
import { useTheme } from './ThemeContext.js';
import { Typography } from '@mui/material';

const LogoutDropdown = ({ handleLogout, userData, profileDropdownRef, setShowProfileDropdown }) => {

    const { currentTheme: theme } = useTheme();

    useEffect(() => {
        const handleDocumentClick = (event) => {
            if ( profileDropdownRef.current && !profileDropdownRef.current.contains(event.target) && !event.target.classList.contains('profile-button')) {
                console.log("triggering setShowProfileDropdown");
                setShowProfileDropdown(false);
            }
        };

        document.addEventListener('mousedown', handleDocumentClick);
        document.addEventListener('mouseup', handleDocumentClick);
        return () => {
            document.removeEventListener('mousedown', handleDocumentClick);
            document.removeEventListener('mouseup', handleDocumentClick);
        };
    }, [setShowProfileDropdown]);

    return (
        <div ref={profileDropdownRef} style={styles.dropdownContainer}>
            <Button2 onClick={() => handleLogout()} style={styles.dropdownButton}>
                Logout
            </Button2>
        </div>
    );
};

const styles = {
    dropdownContainer: {
        position: 'absolute',
        top: '100%',
        right: 0,
        backgroundColor: '#181818',
        boxShadow: '0px 0px 8px 2px rgba(0, 0, 0, 0.1)',
        width: '150px',
        padding: '10px',
        margin: '8px',
        borderRadius: '8px',
        background: '#181818',
        display: 'flex',
        flexDirection: 'column',
    },
    dropdownButton: {
        marginBottom: '8px',
        width: '100%',
    },
};

export default LogoutDropdown;
