import * as React from 'react';
import {Menu} from "@mui/material";

const DefaultMenu = ({anchorEl, open, handleClose, children}) => {
    return (
        <>
            <Menu
                id="demo-positioned-menu"
                aria-labelledby="demo-positioned-button"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: 'center',
                    horizontal: 'center',
                }}
                transformOrigin={{
                    vertical: 'center',
                    horizontal: 'center',
                }}
            >
                {children}
            </Menu>
        </>
    );
};

export default DefaultMenu;