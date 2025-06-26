import {styled} from "@mui/material/styles";
import IconButton from "@mui/material/IconButton";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import * as React from "react";
import MuiDrawer from "@mui/material/Drawer";
import Filter1Icon from "@mui/icons-material/Filter1";
import Filter2Icon from "@mui/icons-material/Filter2";
import Filter3Icon from "@mui/icons-material/Filter3";
import DrawerNavigationButton from "./DrawerNavigationButton";
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import PeopleAltIcon from '@mui/icons-material/PeopleAlt';
import theme from "../../../assets/theme";
import FolderIcon from '@mui/icons-material/Folder';

const drawerWidth = 240;

const openedMixin = (theme) => ({
    width: drawerWidth,
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
    overflowX: 'hidden',
});

const closedMixin = (theme) => ({
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    overflowX: 'hidden',
    width: `calc(${theme.spacing(7)} + 1px)`,
    [theme.breakpoints.up('sm')]: {
        width: `calc(${theme.spacing(8)} + 1px)`,
    },
});

const DrawerHeader = styled('div')(({theme}) => ({
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: theme.spacing(0, 1),
    ...theme.mixins.toolbar,
}));

const MUIStyledDrawer = styled(MuiDrawer, {shouldForwardProp: (prop) => prop !== 'open'})(
    ({theme}) => ({
        width: drawerWidth,
        flexShrink: 0,
        whiteSpace: 'nowrap',
        boxSizing: 'border-box',
        variants: [
            {
                props: ({open}) => open,
                style: {
                    ...openedMixin(theme),
                    '& .MuiDrawer-paper': openedMixin(theme),
                },
            },
            {
                props: ({open}) => !open,
                style: {
                    ...closedMixin(theme),
                    '& .MuiDrawer-paper': closedMixin(theme),
                },
            },
        ],
    }),
);



const Drawer = ({open, handleDrawerClose}) => {

    const navigation = [
        {type: "navigation", icon: <Filter1Icon/>, title: "First", path: "/first"},
        {type: "navigation", icon: <Filter2Icon/>, title: "Second", path: "/second"},
        {type: "navigation", icon: <Filter3Icon/>, title: "Third", path: "/third"},
        {type: "navigation", icon: <FolderIcon/>, title: "Files", path: "/files/1"},
        {type: "navigation", icon: <PeopleAltIcon/>, title: "Users", path: "/users"},
        {type: "navigation", icon: <AccountCircleIcon/>, title: "Profile", path: "/profile"},
        {type: "divider"},
        {type: "navigation", icon: <ChevronRightIcon/>, title: "AppBar", path: "/first"},
        {type: "navigation", icon: <ChevronRightIcon/>, title: "TeacherPanel", path: "/teacher-panel"},
    ]

    return (
        <MUIStyledDrawer variant="permanent" open={open}>
            <DrawerHeader>
                {open && <IconButton onClick={handleDrawerClose}>
                    {theme.direction === 'rtl' ? <ChevronRightIcon/> : <ChevronLeftIcon/>}
                </IconButton>}

            </DrawerHeader>
            <Divider/>
            <List>
                {navigation.map((obj, index) => (
                    obj.type === "navigation" ? (
                        <ListItem key={index} disablePadding sx={{display: 'block'}}>
                           <DrawerNavigationButton open={open} obj={obj}/>
                        </ListItem>
                    ) : ( obj.type === "divider" && (
                            <Divider key={index}/>
                        )
                    )
                ))}
            </List>
        </MUIStyledDrawer>
    )
}

export default Drawer;