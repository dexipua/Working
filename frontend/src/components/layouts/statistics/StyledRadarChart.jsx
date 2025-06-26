import * as React from 'react';
import { RadarChart } from '@mui/x-charts/RadarChart';
import theme from "../../../assets/theme";

const StyledRadarChart = () => {
    return (
        <RadarChart
            height={300}
            series={[{data: [120, 98, 86, 99, 85, 65], fillArea: true}]}
            colors={[theme.palette.primary.main]}
            radar={{
                max: 120,
                metrics: ['Math', 'Chinese', 'English', 'Geography', 'Physics', 'History'],
            }}
        />
    );
}

export default StyledRadarChart;