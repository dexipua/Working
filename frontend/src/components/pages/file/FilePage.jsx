import React, {useEffect, useState} from 'react';
import FileList from "../../common/file/list/FileList";
import {useParams} from "react-router-dom";
import Box from "@mui/material/Box";
import {Divider, Stack, Typography} from "@mui/material";
import {listPanelStyles} from "../../../assets/styles";
import FileUploader from "../../common/file/FileUploader";
import FileService from "../../../services/base/ext/FileService";

const FilePage = () => {
    const {userId} = useParams();
    const [files, setFiles] = useState([]);

    useEffect(() => {
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

        loadFiles();
    }, [userId]);


    const onUploadFile = async (file) => {
        try {
            setFiles(prev => [...prev, file]);
        } catch (e) {
            console.error("Помилка при видаленні файлу", e);
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


    return (
        <>
            <Stack direction="row" sx={listPanelStyles}>
                <Typography variant="h4">Files</Typography>
                <Box sx={listPanelStyles} gap={0.5}>
                    <FileUploader onUploadFile={onUploadFile}/>
                </Box>
            </Stack>

            <Divider sx={{mb: 1, mt: 0.5}}/>

            <FileList files={files} onDelete={deleteFile}/>
        </>
    );
};

export default FilePage;
