import React, { useEffect, useState } from 'react';
import { LinearProgress } from '@mui/material';
import './Notification.css';

const Notification = ({ title, body, onClick, timeout = 5000 }) => {
    const [progress, setProgress] = useState(0);

    useEffect(() => {
        const timer = setInterval(() => {
            setProgress((oldProgress) => {
                const diff = (100 / timeout) * 100;
                return Math.min(oldProgress + diff, 100);
            });
        }, 100);

        return () => {
            clearInterval(timer);
        };
    }, [timeout]);

    useEffect(() => {
        if (progress >= 100) {
            onClick(); // closes 
        }
    }, [progress, onClick]);

    return (
        <div className="notification" onClick={onClick}>

            <div className="notiglow"></div>
            <div className="notiborderglow"></div>
            <div className="notititle">
            <LinearProgress variant="determinate" value={progress} sx={{ width: '95%', borderRadius: '5px', marginBottom: 0.5 }} />
                {title}
            </div>
            <div className="notibody">{body}</div>
        </div>

    );
};

export default Notification;
