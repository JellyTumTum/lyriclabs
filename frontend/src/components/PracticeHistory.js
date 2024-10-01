import React from 'react';
import Avatar from '@mui/material/Avatar';
import { useTheme } from './ThemeContext';
import { List, ListItem, ListItemAvatar, ListItemText, Typography } from '@mui/material';
import TitleCard from './UI Components/TitleCard';

const PracticeHistory = ({ history: historyRecord, count=10 }) => {
    const reversedHistory = historyRecord.slice().reverse();
    const { currentTheme: theme } = useTheme();
    const limitedHistory = reversedHistory.slice(0, count);

    return (
        <List sx={{
            justifyContent: 'center',
            justifyItems: 'center',
            alignContent: 'center',
            alignItems: 'center',
            display: 'flex',
            flexDirection: 'column',
        }}>
            {limitedHistory.map(entry => (
                <ListItem key={entry.id} sx={{ justifyContent: 'center', display: 'flex', width: 'auto' }}>
                    <ListItemAvatar>
                        <Avatar src={entry.artistArt && entry.artistArt.length > 0 ? entry.artistArt[0] : "https://ui-avatars.com/api/?name=M&length=1"} />
                    </ListItemAvatar>
                    <ListItemText
                        primary={entry.artistNames.join(' | ')}
                        secondary={
                            <Typography
                                style={{ color: entry.recentScore === 1 ? 'green' : 'red' }}
                            >
                                {entry.recentScore === 1 ? 'Correct' : 'Incorrect'} | Time: {entry.totalGuessTime}
                            </Typography>
                        }
                    />
                </ListItem>
            ))}
        </List>
    );
};

export default PracticeHistory;
