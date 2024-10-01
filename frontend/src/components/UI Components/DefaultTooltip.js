// DefaultTooltip.js
import React, { useState } from 'react';
import { IconButton, Typography, Tooltip, Box } from '@mui/material';

import HelpOutlinedIcon from '@mui/icons-material/HelpOutlined';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import { useTheme } from './../ThemeContext';

const DefaultTooltip = ({ title, text, placement = 'bottom', opacity = 1 }) => {
    const [open, setOpen] = useState(false);
    const { currentTheme: theme } = useTheme();

    const handleToggle = () => {
        setOpen(!open); // Toggle the open state
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <ClickAwayListener onClickAway={handleClose}>
            <Tooltip
                PopperProps={{
                    disablePortal: true,
                }}
                onClose={handleClose}
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
                open={open}

                disableFocusListener
                disableHoverListener
                disableTouchListener
                title={
                    <>
                        <Box sx={{
                            opacity: opacity,
                            border: `1px solid ${theme.palette.primary.main}`,
                            borderRadius: '2px',
                            padding: theme.spacing(2),
                            backgroundColor: theme.palette.background.two,
                        }}>
                            {title && <Typography variant="body1" component="div" sx={{ marginBottom: theme.spacing(2), color: theme.palette.primary.main }}>
                                {title}
                            </Typography>}
                            {text && <Typography variant="body2" sx={{ color: theme.palette.text.secondary }}>
                                {text}
                            </Typography>}

                        </Box>


                    </>}
                placement={placement}
            >
                <IconButton sx={{opacity: opacity}} onClick={handleToggle}>
                    <HelpOutlinedIcon sx={{ color: open ? theme.palette.primary.main : 'white' }} />
                </IconButton>
            </Tooltip>
        </ClickAwayListener>
    );
};

export default DefaultTooltip;
