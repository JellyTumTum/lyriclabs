import React from 'react';
import Button2 from './UI Components/Buttons/Button2';
import CustomCard from './UI Components/CustomCard';
import { useNavigate } from 'react-router-dom';
import TitleCard from './UI Components/TitleCard';
import { Typography, Grid } from '@mui/material';
import ScienceTwoToneIcon from '@mui/icons-material/ScienceTwoTone';

const QrPage = () => {

    const navigate = useNavigate();

    const goHome = () => {
        navigate('/lyriclabs');
    }

    return (

        <Grid container spacing={2} style={{ height: 'auto' }}>
            <Grid item xs={12} sx={{ margin: 5 }}>
                <CustomCard >
                    <div style={{ margin: 10 }}>
                        <TitleCard title="Welcome">
                        </TitleCard>
                    </div>
                    <Typography variant="body1" component="p">
                        Thank you for scanning the code. you can read about the website on the home page, or using the button below (they are the same button I just placed another one here)
                    </Typography>
                    <ScienceTwoToneIcon onClick={goHome} className="hoverableIcon" sx={{margin: 5}}></ScienceTwoToneIcon>


                </CustomCard>
            </Grid>
        </Grid>


    );
};

export default QrPage;
