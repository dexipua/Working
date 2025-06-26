import * as React from 'react';
import {PieChart} from '@mui/x-charts/PieChart';
import theme from "../../../assets/theme";

const StyledPieChart = () => {
    return (
        <PieChart
            series={[
                {
                    data: [
                        { id: 0, value: 15, label: 'Done', color: theme.palette.primary.main },
                        { id: 1, value: 10, label: 'Todo', color: '#d6d6d6' },
                    ],
                    innerRadius: 35,
                    paddingAngle: 2,
                    cornerRadius: 4,
                },
            ]}
            height={220}
            sx={{
                ml: '70px'
            }}
        />
    );
}

export default StyledPieChart;