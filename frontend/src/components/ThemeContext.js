// ThemeContext.js
import React, { createContext, useContext, useState, useMemo } from 'react';
import { ThemeProvider as MUIThemeProvider } from '@mui/material/styles';
import { Dark, Light } from './Themes';
import { CssBaseline } from '@mui/material';

const ThemeContext = createContext();

export const useTheme = () => useContext(ThemeContext);

export const ThemeProvider = ({ children }) => {
    const [currentTheme, setCurrentTheme] = useState(() => {
        const savedTheme = localStorage.getItem('Theme');
        if (savedTheme == 'Light') return Dark;
        else return Dark; 
    });

    const theme = useMemo(() => {
        return currentTheme === 'Dark' ? Dark : Light;
    }, [currentTheme]);

    // Function to switch theme and save (probably wont be used but while im tuned into the themeing mindset)
    const switchTheme = (themeName) => {
        setCurrentTheme(themeName);
        localStorage.setItem('Theme', themeName);
    };

    return (
        <ThemeContext.Provider value={{ currentTheme, switchTheme }}>
            <MUIThemeProvider theme={theme}>
                <CssBaseline />
                {children}
            </MUIThemeProvider>
        </ThemeContext.Provider>
    );
};
