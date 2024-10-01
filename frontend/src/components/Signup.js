import React, { useState } from 'react';
import axios from 'axios';
import './UI Components/SignupPanel.css';
import { useTheme } from './ThemeContext.js';
import Button2 from './UI Components/Buttons/Button2';
import { Typography, Checkbox, FormControlLabel } from '@mui/material';
import TitleCard from './UI Components/TitleCard';
import LoginResponse from '../api/LoginResponse.js';
import { useNavigate } from 'react-router-dom';
import DefaultTooltip from './UI Components/DefaultTooltip.js';

const Signup = () => {

    const navigate = useNavigate();
    const { currentTheme: theme } = useTheme();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [username, setUsername] = useState('');
    const [responseMessage, setResponseMessage] = useState('');
    const [tickedBox, setTickedBox] = useState(false);

    const handleSignup = async () => {
        try {
            const response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/auth/register`, {
                username,
                email,
                password,
            });

            if (response.data.success) {
                setResponseMessage("Account created successfully");
                handleLogin();
            } else {
                setResponseMessage('Email already attached to an account, you can login at the top right');
            }
        } catch (error) {
            console.log(error.response + "GSDFSDG");
            console.log(error.response.data.responseMessage);
        }
    };

    const handleLogin = async () => {
        console.log("handleLogin function is being executed.");
        try {
            let response;
            response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/auth/login`, {
                username: username,
                email: '',
                password,
            }, {
                withCredentials: true
            });
            console.log(response + "usingUsername");
            const loginResponse = new LoginResponse(
                response.data.userId,
                response.data.username,
                response.data.email,
                response.data.jwt,
                response.data.responseMessage
            );
            setResponseMessage(loginResponse.responseMessage);

            if (loginResponse.responseMessage.includes("successful")) {
                // succesful login
                localStorage.setItem('jwtToken', loginResponse.jwt);
                localStorage.setItem('notification', 'Logged In|You have been logged in as ' + loginResponse.username);
                window.location.href = ('/lyriclabs');

            }
        } catch (error) {
            console.log(error);
        }
    };

    return (
        <div style={styles.container}>
            <div className="background-container">
                <div className="signup-box">
                    <div style={{ marginBottom: 50, padding: 2 }}>
                        <TitleCard title="Signup">

                        </TitleCard>
                    </div>

                    <div className="user-box">
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 20 }}>

                            <input type="text" name="" required="" value={username} onChange={(e) => setUsername(e.target.value)} />
                            <DefaultTooltip text="Usernames cannot be changed and are publicly viewable" placement="top"></DefaultTooltip>
                            <label>Username</label>
                        </div >


                    </div>
                    {/* <Typography sx={{ mb: 5, color: theme.palette.text.secondary }}>Usernames cannot be changed and are publicly viewable</Typography> */}
                    <div className="user-box">
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: 20 }}>
                            <input type="text" name="" required="" value={email} onChange={(e) => setEmail(e.target.value)} />
                            <DefaultTooltip text="Emails are not used for anything, just required incase of contact" placement="top"></DefaultTooltip>
                            <label>Email</label>
                        </div >
                    </div>
                    <div className="user-box">
                        <input type="password" name="" required="" value={password} onChange={(e) => setPassword(e.target.value)} />
                        <label>Password</label>
                    </div>
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={tickedBox}
                                onChange={(e) => setTickedBox(e.target.checked)}
                                name="checkedB"
                                color="primary"
                                sx={{ color: theme.palette.primary.main }}
                            />
                        }
                        label="I understand that information about my responses will be stored for analysis purposes."
                        sx={{ color: theme.palette.text.secondary, marginY: 2 }}
                    />
                    <center>
                        <Button2 variant="contained" color="primary" onClick={handleSignup} disabled={!tickedBox}>
                            Create Account
                        </Button2>
                        {responseMessage && <p>{responseMessage}</p>}
                    </center>

                    {/* </form> */}
                </div>
            </div>
        </div>
    );
};

const styles = {
    container: {
        overflow: 'hidden',
        height: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: '#f0f0f0',
    },
    island: {
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0px 0px 8px 2px rgba(0, 0, 0, 0.1)',
        background: '#ffffff',
        maxWidth: '400px',
    },
    button: {
        marginTop: '10px',
    },
};

export default Signup;

/* 

TODO:
fix styling for messages and complete tickbox functionality

*/