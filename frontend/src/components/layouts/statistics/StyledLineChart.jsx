import * as React from 'react';
import { LineChart } from '@mui/x-charts/LineChart';
import theme from "../../../assets/theme";

const dataset = [
    { x: 1, y: 75 },
    { x: 2, y: 5.5 },
    { x: 3, y: 2 },
    { x: 5, y: 50 },
    { x: 8, y: 1.5 },
    { x: 10, y: 100 },
];

const StyledLineChart = () => {
    return (
        <LineChart
            dataset={dataset}
            xAxis={[{ dataKey: 'x' }]}
            series={[{ dataKey: 'y' }]}
            colors={[theme.palette.primary.main]}
            grid={{ vertical: true, horizontal: true }}
            height={310}
        />
    );
}

export default StyledLineChart;