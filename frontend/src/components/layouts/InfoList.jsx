import React from 'react';
import {Typography} from "@mui/material";
import Box from "@mui/material/Box";

const InfoList = ({list, textAlign}) => {
    return (
        <table>
            <tbody>
            {list.map(({icon, label, value}, index) => (
                <tr key={index}>
                        <td>
                            <Typography noWrap variant="body1" sx={{display: 'flex', alignItems: 'center', gap: 0.5}}>
                                {icon}
                                {label}
                            </Typography>
                        </td>
                        <td style={{textAlign: textAlign}}>
                            <Typography component="div">{value}</Typography>
                        </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default InfoList;