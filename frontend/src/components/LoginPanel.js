import React, { useState, useEffect, useRef } from 'react';
import { FormGroup, FormControlLabel, Switch } from '@mui/material';
import LoginResponse from '../api/LoginResponse';
import axios from 'axios';
import { useTheme } from './ThemeContext.js';
import './UI Components/LoginPanel.css';
import Button2 from './UI Components/Buttons/Button2.js';
import { Typography } from '@mui/material';

const LoginPanel = ({ loginPanelRef, setShowLoginPanel }) => {

    const { currentTheme: theme } = useTheme();
    const [loginMethod, setLoginMethod] = useState('');
    const usernameRef = useRef(null);
    const passwordRef = useRef(null);
    const [password, setPassword] = useState('');
    const [responseMessage, setResponseMessage] = useState('');
    const [rawResponse, setRawResponse] = useState('');
    const [isLogin, setIsLogin] = useState(true);

    const handleSignup = async (event) => {
        event.preventDefault();
        const username = usernameRef.current.value;
        const password = passwordRef.current.value;
        console.log(username);
        console.log(password);
        try {
            const response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/auth/register`, {
                username,
                password,
            });
            if (response.data.success) {
                setResponseMessage("Account created successfully");
                handleLogin(event);
            } else {
                setResponseMessage(response.data.responseMessage);
            }
        } catch (error) {
            console.log(error.response + "GSDFSDG");
            console.log(error.response.data.responseMessage);
        }
    };


    const handleLogin = async (event) => {
        event.preventDefault();
        // Use refs to access input values
        const loginMethod = usernameRef.current.value;
        const password = passwordRef.current.value;
        console.log("handleLogin function is being executed.");
        try {
            const usingEmail = loginMethod.includes("@");
            let response;
            if (usingEmail) {
                response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/auth/login`, {
                    username: '',
                    email: loginMethod,
                    password,
                }, {
                    withCredentials: true
                });
            } else {
                response = await axios.post(`${process.env.REACT_APP_SERVER_URL}/auth/login`, {
                    username: loginMethod,
                    email: '',
                    password,
                }, {
                    withCredentials: true
                });
            }

            setRawResponse(JSON.stringify(response.data, null, 2));
            const loginResponse = new LoginResponse(
                response.data.userId,
                response.data.username,
                response.data.email,
                response.data.jwt,
                response.data.responseMessage
            );
            setResponseMessage(loginResponse.responseMessage);
            if (loginResponse.responseMessage.includes("successful")) {
                // Successful login
                localStorage.setItem('jwtToken', loginResponse.jwt);
                localStorage.setItem('notification', 'Logged In|You have successfully been logged in as ' + loginResponse.username);
                window.location.reload();
            }
        } catch (error) {
            console.log(error);
        }
    };


    useEffect(() => {
        const handleDocumentClick = (event) => {
            if (loginPanelRef.current && !loginPanelRef.current.contains(event.target) && !event.target.classList.contains('button')) {
                console.log("triggering setShowLoginPanel");
                setShowLoginPanel(false);
            }
        };
    
        document.addEventListener('mousedown', handleDocumentClick);
        return () => {
            document.removeEventListener('mousedown', handleDocumentClick);
        };
    }, [setShowLoginPanel]);

    return (
        <div ref={loginPanelRef} className="login-box">
            <form onSubmit={isLogin ? handleLogin : handleSignup}>
                <div className="user-box">
                    <input ref={usernameRef} type="text" name="" required="" />
                    <label>Username</label>
                </div>
                <div className="user-box">
                    <input ref={passwordRef} type="password" name="" required="" />
                    <label>Password</label>
                </div>

                {responseMessage && !responseMessage.includes('successful') && <p style={{ color: theme.palette.error.main }}>{responseMessage}</p>}
                <center>
                    <Button2 onClick={isLogin ? handleLogin : handleSignup}>
                        <Typography >{isLogin ? "Login" : "Signup"}</Typography>
                    </Button2>
                    <FormGroup>
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={isLogin}
                                    onChange={() => setIsLogin(!isLogin)}
                                    color="primary"
                                    sx={{ color: theme.palette.primary.main }}
                                />
                            }
                            label={isLogin ? "Login" : "Signup"}
                            labelPlacement="start"
                            sx={{ marginLeft: 'auto', marginRight: 'auto', marginBottom: 2, color: theme.palette.primary.main }}
                        />
                    </FormGroup>
                    <Typography sx={{ color: theme.palette.text.secondary, fontSize: '14px', marginTop: 2 }}>
                        {isLogin ? "Not got an account? switch over to signup above" : ""}
                    </Typography>

                </center>
            </form>
        </div>
    );
};

const styles = {
    loginContainer: {
        position: 'absolute',
        top: '100%',
        right: 0,
        backgroundColor: '#ffffff',
        boxShadow: '0px 0px 8px 2px rgba(0, 0, 0, 0.1)',
        width: '300px',
        padding: '20px',
        margin: '8px',
        borderRadius: '8px',
        background: '#ffffff',

    },
    inputField: {
        marginBottom: '10px',
    },
    loginButton: {
        marginTop: '10px',
    },
};

export default LoginPanel;
