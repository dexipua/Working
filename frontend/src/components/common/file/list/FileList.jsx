import React from 'react';
import {Grid} from "@mui/material";
import FileBox from "./FileBox";

const FileList = ({files, onDelete}) => {
    return (
        <Grid container spacing={1.5}>
            {files.map((file, index)  => (
                <Grid item xs={12} sm={6} md={4} lg={3} key={index}>
                    <FileBox file={file} onDelete={onDelete} />
                </Grid>
            ))}
        </Grid>
    );
};

export default FileList;
