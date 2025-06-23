import React from 'react';
import {Box, IconButton, Typography} from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import {listElementBoxStyle, listElementBoxTextStyle} from "../../../../assets/styles";

const FileBox = ({file, onDelete}) => {

    const handleDownload = () => {
        const link = document.createElement("a");
        link.href = file.path;
        link.download = file.fileName;
        link.click();
    };

    return (
        <Box sx={{...listElementBoxStyle, position: 'relative'}} onClick={handleDownload}>
            {/* Якщо це зображення – покажемо прев'ю */}
            {file.fileType.startsWith("image/") && (
                <Box
                    component="img"
                    src={file.path}
                    alt={file.fileRealName}
                    sx={{width: "100%", height: 160, objectFit: "cover", borderRadius: 2, mb: 1}}
                />
            )}

            <Typography noWrap variant="body2" sx={listElementBoxTextStyle}>
                {file.fileRealName}
            </Typography>

            <Typography variant="caption" sx={{...listElementBoxTextStyle, opacity: 0.6}}>
                {file.fileType}
            </Typography>

            <IconButton
                onClick={(e) => {
                    e.stopPropagation();
                    onDelete(file);
                }}
                sx={{position: "absolute", top: 4, right: 4}}
                color="error"
                size="small"
            >
                <DeleteIcon fontSize="small"/>
            </IconButton>
        </Box>
    );
};

export default FileBox;
