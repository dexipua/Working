import {createTheme} from "@mui/material/styles";
import "@fontsource/open-sans";

const theme = createTheme({
    palette: {
        primary: {
            main: '#ad1457',
            dark: '#880e4f',
            light: '#d81b60'
        },
    },
    typography: {
        fontFamily: "Open Sans, Arial, sans-serif",
    },
    components: {
        MuiAppBar: {
            styleOverrides: {
                colorDefault: ({theme}) => ({
                    backgroundColor:
                        theme.palette.mode === 'dark'
                            ? '#000000'
                            : '#fff',
                }),
            },
        },
    },
});

export default theme;
