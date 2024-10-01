import React from 'react';
import { Avatar, Typography, Grid, Paper, Card } from '@mui/material';
import { useTheme } from './ThemeContext.js';

const RoundResultsDisplay = ({ stats }) => {

    const { currentTheme: theme } = useTheme();
    return (
        <Grid container spacing={2} justifyContent="center" alignItems="center">
            {stats.map((stat, index) => (
                <Grid item key={index} xs={4} sm={3} md={2} lg={4} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column' }}>
                    <Paper sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', padding: '5px', backgroundColor: theme.palette.background.two, color: theme.palette.text.main }}>
                        <Avatar src={stat.frontendArtist.artURL} sx={{ width: 56, height: 56 }} />
                        <Typography variant="body1" component="h2" style={{ marginTop: '5px', fontSize: '16px' }}>{stat.frontendArtist.artistName}</Typography>
                        <Typography variant="body1" style={{ fontSize: '14px', color: stat.totalRounds === 0 ? theme.palette.text.secondary : stat.correctGuesses == stat.totalRounds ? theme.palette.primary.main : (stat.correctGuesses == 0 ? theme.palette.error.main : theme.palette.text.main) }}>
                            {stat.correctGuesses}/{stat.totalRounds} {isNaN(stat.percentCorrect) ? "" : `(${Math.round(stat.percentCorrect)}%)`}
                        </Typography>
                    </Paper>
                </Grid>
            ))}
        </Grid>
    );
};

export default RoundResultsDisplay;