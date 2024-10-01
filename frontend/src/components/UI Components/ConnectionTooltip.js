// DefaultTooltip.js
import React, { useState } from 'react';
import { useTheme } from './../ThemeContext';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import { IconButton, Typography, Tooltip, Box } from '@mui/material';

import WifiIcon from '@mui/icons-material/Wifi';
import WifiOffIcon from '@mui/icons-material/WifiOff';

const ConnectionTooltip = ({ connected, placement = 'top' }) => {

    const { currentTheme: theme } = useTheme();
    const [open, setOpen] = useState(false);

    const handleClose = () => {
        setOpen(false);
    };

    const handleToggle = () => {
        setOpen(!open); // Toggle the open state
    };

    return (
        <ClickAwayListener onClickAway={handleClose}>
            <Tooltip
                PopperProps={{
                    disablePortal: true,
                }}
                onClose={handleClose}
                open={open}
                disableFocusListener
                disableHoverListener
                disableTouchListener
                componentsProps={{
                    tooltip: {
                        sx: {
                            bgcolor: 'transparent',
                            '& .MuiTooltip-arrow': {
                                color: 'transparent',
                            },
                        },
                    },
                }}
                title={
                    <>
                        <Box sx={{
                            border: `1px solid ${theme.palette.primary.main}`,
                            borderRadius: '2px',
                            padding: theme.spacing(2),
                            backgroundColor: theme.palette.background.two,
                        }}>
                            <Typography variant="body1" component="div" sx={{ marginBottom: theme.spacing(2), color: theme.palette.primary.main }}>
                                Connection Status
                            </Typography>
                            <Typography variant="body1" sx={{ color: theme.palette.text.secondary }}>
                                {connected ? "Stable" : "Not so stable (you are currently not connected)"}
                            </Typography>

                        </Box>


                    </>}
                placement={placement}
            >
                <IconButton onClick={handleToggle}>
                    {connected ? (
                        <WifiIcon sx={{ color: theme.palette.primary.main }} />
                    ) : (
                        <WifiOffIcon sx={{ color: theme.palette.error.main }} />
                    )}
                </IconButton>
            </Tooltip>
        </ClickAwayListener>
    );
};

export default ConnectionTooltip;
