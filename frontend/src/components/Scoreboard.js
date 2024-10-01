// Scoreboard.js
import React from 'react';
import Avatar from '@mui/material/Avatar';
import { List } from '@mui/material';
import ListItem from '@mui/material/ListItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemText from '@mui/material/ListItemText';
import SignalCellularConnectedNoInternet4BarIcon from '@mui/icons-material/SignalCellularConnectedNoInternet4Bar';
import TitleCard from './UI Components/TitleCard';

const Scoreboard = ({ users }) => {
    const sortedUsers = [...users].sort((a, b) => b.score - a.score);

    return (
        <List sx={{
            justifyContent: 'center',
            justifyItems: 'center',
            alignContent: 'center',
            alignItems: 'center',
            display: 'flex',
            flexDirection: 'column',
        }}>
            {sortedUsers.map(user => (
                <ListItem key={user.id} sx={{ justifyContent: 'center', display: 'flex', width: '75%' }}>
                    <ListItemAvatar>
                        <Avatar src={`https://ui-avatars.com/api/?name=${user.id}&length=3`} />
                    </ListItemAvatar>
                    <ListItemText sx={{
                        color: 'white',
                        '& .MuiListItemText-secondary': {
                            color: 'white',
                        },
                        textAlign: 'center'
                    }} primary={user.id} secondary={`Score: ${user.score}`} />
                    {/* {!user.isConnected && <SignalCellularConnectedNoInternet4BarIcon color="secondary" />} never implemented properly anyway dont believe */}
                </ListItem>
            ))}
        </List>
    );
};

export default Scoreboard;
