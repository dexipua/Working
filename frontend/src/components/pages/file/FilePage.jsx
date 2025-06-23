import React, { useEffect, useState } from 'react';
import FileList from "../../common/file/list/FileList";
import {useParams} from "react-router-dom";
import FileService from "../../../services/base/ext/FileService";
import Box from "@mui/material/Box";
import {Typography} from "@mui/material";

const FilePage = () => {
    const [files, setFiles] = useState([]);
    const {userId} = useParams();

    const loadFiles = async () => {
        try {
            console.log(userId)
            const response = await FileService.getAllFiles(userId, 0, 10);
            console.log(response)
            setFiles(response.content || []);
        } catch (e) {
            console.error("Не вдалося завантажити файли", e);
        }
    };

    const deleteFile = async (file) => {
        try {
            await FileService.deleteFile(file.id);
            setFiles(prev => prev.filter(f => f.id !== file.id));
        } catch (e) {
            console.error("Помилка при видаленні файлу", e);
        }
    };

    useEffect(() => {
        if (userId) {
            loadFiles();
        }
    }, [userId]);

    return (
        <Box sx={{
            width: 1500,
            border: '1px solid #ddd',
            padding: '20px',
            margin: '10px',
            borderRadius: "10px",
            display: "flex",
            flexDirection: "column"
        }}>
            <Typography variant="h4">User`s Files</Typography>
            <FileList files={files} onDelete={deleteFile} />
        </Box>
    );
};

export default FilePage;
