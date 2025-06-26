import React from 'react';
import theme from "../../../assets/theme";
import {FormControl, InputLabel, MenuItem, Select} from "@mui/material";

const styles = {
    minWidth: 90,
    '& .MuiSelect-root': {
        color: theme.palette.primary.main,
    },
    '& .MuiInputLabel-root': {
        color: theme.palette.primary.main,
    },
    '& .MuiOutlinedInput-root': {
        '& fieldset': {
            borderColor: theme.palette.primary.main,
            borderWidth: 1.5,
        },
        '&:hover fieldset, &.Mui-focused fieldset': {
            borderColor: theme.palette.primary.main,
            borderWidth: 1.5,
        },
    },
};

const FilterSelect = ({ label, value, setValue, options }) => {
    return (
        <FormControl size="small" sx={styles}>
            <InputLabel>{label}</InputLabel>
            <Select value={value} onChange={(e) => setValue(e.target.value)} label={label} variant="outlined">
                {options.map((option, index) => (
                    <MenuItem key={index} value={option.value}>
                        {option.label}
                    </MenuItem>
                ))}
            </Select>
        </FormControl>
    );
};

export default FilterSelect;