// LegalNotification.js
import React from 'react';
import { Paper, Grid, Card } from '@mui/material';
import { useTheme } from './../ThemeContext.js';

const LegalNotification = ({ spotify = false, musixmatch = false }) => {

    const { currentTheme: theme } = useTheme();

    const cardStyling = {
        backgroundColor: theme.palette.background.two,
                            height: '80%',
                            width: '20%',
                            padding: 1
    }


    return (
        <Paper elevation={12} sx={{ padding: 2, backgroundColor: theme.palette.background.one }}>
            <Grid container justifyContent="center" alignItems="center">
                <Grid item xs={6} container justifyContent="center">
                    {spotify && (
                        <Card sx={cardStyling}>
                            <a href="https://www.spotify.com" target="_blank" rel="noopener noreferrer">
                                <img
                                    src={`${process.env.PUBLIC_URL}/spotify-logo-green.svg`}
                                    alt="Spotify Logo"
                                    style={{ maxWidth: '100%', maxHeight: '6vh' }}
                                />
                            </a>
                        </Card>
                    )}
                </Grid>
                <Grid item xs={6} container justifyContent="center">
                    {musixmatch && (
                        <Card sx={cardStyling}>
                            <a href="https://www.musixmatch.com" target="_blank" rel="noopener noreferrer">
                                <img
                                    src={`${process.env.PUBLIC_URL}/musixmatch-icon-white.png`} // could potentially change to a red one
                                    alt="Musixmatch Logo"
                                    style={{ maxWidth: '100%', maxHeight: '6vh' }}
                                />
                            </a>
                        </Card>
                    )}
                </Grid>
            </Grid>
        </Paper>
    );
    
};

export default LegalNotification;
