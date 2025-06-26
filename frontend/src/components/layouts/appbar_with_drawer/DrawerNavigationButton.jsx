import React from 'react';
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import {useLocation, useNavigate} from "react-router-dom";
import theme from "../../../assets/theme";

const DrawerNavigationButton = ({open, obj}) => {
    const navigate = useNavigate();
    const location = useLocation();

    const isActive = location.pathname === obj.path;
    return (
        <>
            <ListItemButton onClick={() => navigate(obj.path)} sx={[{
                minHeight: 48,
                px: 2.5,
            }, open ? {justifyContent: 'initial',} : {justifyContent: 'center',},]}>
                <ListItemIcon sx={[{
                    minWidth: 0,
                    justifyContent: 'center',
                    color: isActive ? theme.palette.primary.main : "primary",
                }, open ? {mr: 3,} : {mr: 'auto',}, isActive && {color: theme.palette.primary.main},]}>
                    {obj.icon}
                </ListItemIcon>
                <ListItemText primary={obj.title} sx={[open ? {opacity: 1,} : {opacity: 0,},]}/>
            </ListItemButton>
        </>
    );
};

export default DrawerNavigationButton;