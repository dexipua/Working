import React, {useState} from "react";
import {Button, Typography} from "@mui/material";
import FileService from "../../../services/base/ext/FileService";

const FileUploader = ({onUploadFile}) => {
    const [status, setStatus] = useState()

    const handleFileChange = async (e) => {
        console.log('handleFileChange')
        await uploadFile(e.target.files[0])
    };

    const computeHash = async (file) => {
        const arrayBuffer = await file.arrayBuffer();
        const hashBuffer = await crypto.subtle.digest("SHA-256", arrayBuffer);
        const hashArray = Array.from(new Uint8Array(hashBuffer));
        return hashArray.map(b => b.toString(16).padStart(2, "0")).join("");
    };

    const uploadFile = async (file) => {
        if (!file) return;

        setStatus("Обчислення хешу...");
        const hash = await computeHash(file);

        setStatus("Завантаження файлу...");
        try {
            const response = await FileService.uploadFile(file, hash, 1);
            onUploadFile(response);
            setStatus(" ");

        } catch (err) {
            setStatus("Помилка при завантаженні");
        }
    };

    return (
        <>
            <input
                id="file-upload"
                type="file"
                style={{display: "none"}}
                onChange={handleFileChange}
            />

            <label htmlFor="file-upload">
                <Button variant="contained" component="span">
                    Choose File
                </Button>
            </label>

            <Typography variant="body2" color="textSecondary">
                {status}
            </Typography>
        </>
    )
};

export default FileUploader;
