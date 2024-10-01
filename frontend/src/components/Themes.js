import { createTheme } from '@mui/material/styles';

const themes = {
    Dark: createTheme({
        palette: {
            type: 'dark',
            mode: 'dark',
            primary: { main: '#1DB954', darker: "#14823B" },  // Spotify Green
            secondary: { main: '#121212', dark: '#0A0A0A' }, // Spotify Black
            background: {default: '#1E1E1E', one: '#181818', two: '#1E1E1E', three: '#1F1F1F', paper: '#1F1F1F'},
            text: {main: '#FFFFFF', secondary: '#bdb8b8'},
            musixmatch: {main: '#E72C40'},
            error: { main: '#FF0000' },
            warning: { main: '#750000' },
            info: { main: '#00AD79' },
            success: { main: '#000A02' }, 
        },
        components: {
            MuiTextField: {
              styleOverrides: {
                root: {
                  '& label': {
                    color: '#FFFFFF',
                  },
                  '& label.Mui-focused': {
                    color: '#1DB954', 
                  },
                  '& .MuiOutlinedInput-root': {
                    '& fieldset': {
                      borderColor: '#FFFFFF', 
                    },
                    '&:hover fieldset': {
                      borderColor: '#1DB954', 
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#1DB954', 
                    },
                    '& input': {
                      color: '#FFFFFF',
                    },
                  },
                  '& .MuiInput-underline:before': {
                    borderBottomColor: '#FFFFFF', 
                  },
                  '& .MuiInput-underline:hover:not(.Mui-disabled):before': {
                    borderBottomColor: '#1DB954', 
                  },
                  '& .MuiInput-underline:after': {
                    borderBottomColor: '#1DB954',
                  },
                },
              },
            },
        }
    }),

    Light: createTheme({
        palette: {
            type: 'light',
            primary: { main: '#1DB954', darker: "#14823B" },
            secondary: { main: '#191414', dark: '#0A0A0A' },
            background: {one: '#FFFFFF', two: '#C2C2C2', three: '#EEEEEE'},
            text: {main: '#000000'},
            error: { main: '#FF0000' },
            warning: { main: '#750000' },
            info: { main: '#00AD79' }, 
            success: { main: '#000A02' },
        },
    }),
}

export const {
    Dark, 
    Light
} = themes;

