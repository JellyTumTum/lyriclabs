import React from 'react';
import Avatar from '@mui/material/Avatar';
import { List, Typography } from '@mui/material';
import ListItem from '@mui/material/ListItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import DefaultTooltip from './UI Components/DefaultTooltip';
import TitleCard from './UI Components/TitleCard';

const History = ({ history: historyRecord }) => {
    // reverse list so that it works like a history. 
    const reversedHistory = historyRecord.slice().reverse();

    return (
        <List sx={{
            justifyContent: 'center',
            justifyItems: 'center',
            alignContent: 'center',
            alignItems: 'center',
            display: 'flex',
            flexDirection: 'column',
        }}>
            {reversedHistory.map(entry => (
                <ListItem key={entry.id} sx={{ justifyContent: 'center', display: 'flex', width: '75%' }}>
                    <ListItemAvatar>
                        <Avatar src={`https://ui-avatars.com/api/?name=${entry.username}&length=3`} />
                    </ListItemAvatar>
                    <ListItemText
                        primary={entry.username}
                        secondary={`Score: ${entry.recentScore} | Time : ${(entry.totalGuessTime / 1000).toFixed(2)}s`}
                        sx={{
                            color: 'white',
                            '& .MuiListItemText-secondary': {
                                color: 'white',
                            },
                            textAlign: 'center'
                        }}
                    />
                </ListItem>
            ))}
        </List>
    );
};

export default History;
