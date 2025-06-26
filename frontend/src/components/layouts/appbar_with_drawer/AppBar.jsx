import * as React from 'react';
import {styled} from '@mui/material/styles';
import MuiAppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import Drawer from "./Drawer";
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import {Stack} from "@mui/material";
import Box from "@mui/material/Box";
import AccountMenu from "./AccountMenu";
import theme from "../../../assets/theme";

const drawerWidth = 240;

const MuiStyledAppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== 'open',
})(({theme}) => ({
    borderBottom: '1px solid #ddd',
    boxShadow: 'none',
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['width', 'margin'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    variants: [
        {
            props: ({open}) => open,
            style: {
                marginLeft: drawerWidth,
                width: `calc(100% - ${drawerWidth}px)`,
                transition: theme.transitions.create(['width', 'margin'], {
                    easing: theme.transitions.easing.sharp,
                    duration: theme.transitions.duration.enteringScreen,
                }),
            },
        },
    ],
}));

const AppBar = () => {
    const [open, setOpen] = React.useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    return (
        <>
            <CssBaseline/>
            <MuiStyledAppBar position="fixed" open={open} color={'default'}>
                <Toolbar sx={{display: "flex", justifyContent: "space-between", alignItems: "center",}}>
                    <Stack direction="row" alignItems={"center"}>
                        <IconButton
                            onClick={handleDrawerOpen}
                            edge="start"
                            sx={[
                                {marginRight: 5,},
                                open && {display: 'none'},
                                theme.palette.mode === 'dark' ? {
                                    color: theme.palette.grey[50]
                                } : {
                                    color: theme.palette.grey[800]
                                }]
                            }
                        >
                            <MenuIcon/>
                        </IconButton>

                        <AccountBalanceIcon color='primary' sx={{mt: '3px', mr: '4px', fontSize: '27px'}}/>
                        <Typography variant="h6" fontWeight='bold' noWrap>
                            Preparations
                        </Typography>
                    </Stack>

                    <Box sx={{display: "flex", alignItems: "center",}}>
                        <AccountMenu/>
                    </Box>
                </Toolbar>
            </MuiStyledAppBar>
            <Drawer open={open} handleDrawerClose={handleDrawerClose}/>
        </>
    );
}

export default AppBar;


