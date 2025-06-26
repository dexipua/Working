import React from 'react';
import {Box, Stack} from "@mui/material";
import Typography from "@mui/material/Typography";
import StyledLineChart from "../../../layouts/statistics/StyledLineChart";
import StyledRadarChart from "../../../layouts/statistics/StyledRadarChart";
import StyledPieChart from "../../../layouts/statistics/StyledPieChart";


const Statistics = () => {
    return (
        <Stack direction={'column'} spacing={"15px"}>
            <Box sx={{
                maxHeight: 350,
                height: '100%',
                border: '1px solid #ddd',
                padding: '10px',
                borderRadius: "10px",
                display: 'flex',
                flexDirection: 'column'
            }}>
                <Typography
                    sx={{fontWeight: "bold", alignItems: 'center', textAlign: 'center', mt: '10px'}}>
                    Statistics
                </Typography>

                <StyledLineChart/>

            </Box>
            <Box>
                <Stack direction={'row'} spacing={'15px'}>
                    <Box sx={{
                        width: '100%',
                        border: '1px solid #ddd',
                        padding: '10px',
                        borderRadius: "10px",
                        display: 'flex',
                        flexDirection: 'column'
                    }}>
                        <Typography sx={{fontWeight: "bold", textAlign: 'center', mt: '10px'}}>
                            Statistics
                        </Typography>
                        <StyledRadarChart/>
                    </Box>

                    <Box sx={{
                        width: '100%',
                        border: '1px solid #ddd',
                        padding: '10px',
                        borderRadius: "10px",
                        display: 'flex',
                        flexDirection: 'column'
                    }}>
                        <Typography sx={{fontWeight: "bold", textAlign: 'center', mt: '10px'}}>
                            Statistics
                        </Typography>
                        <StyledPieChart/>
                    </Box>
                </Stack>
            </Box>
        </Stack>
    );
};

export default Statistics;