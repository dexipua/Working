import React from 'react';
import Box from "@mui/material/Box";
import AppBar from "./AppBar";

const PageContainer = ({children}) => {
    return (
        <Box sx={{ display: 'flex' }}>
            <AppBar/>
            <Box component="main" sx={{ flexGrow: 1, p: 3, mt: '55px' }}>
                {children}

            </Box>
        </Box>
    );
};

export default PageContainer;