import React from 'react';
import DefaultMenu from "../../../layouts/DefaultMenu";
import {IconButton} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import EditIcon from "@mui/icons-material/Edit";

const AvatarMenu = ({isIcon, setHover, uploadProfileIcon, deleteProfileIcon}) => {
    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);

    const handleClick = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
        setHover(false)

    };

    const handleUploadFile = (e) => {
        uploadProfileIcon(e.target.files[0]);
        handleClose();
    };

    const handleDeleteFile = async () => {
        deleteProfileIcon()
        handleClose();
    };


    return (
        <>
            <IconButton onClick={handleClick} sx={{width: "100%", height: "100%", borderRadius: "50%"}}>
                <EditIcon color="primary" sx={{fontSize: 80, padding: 0, margin: 0}}/>
            </IconButton>

            <DefaultMenu anchorEl={anchorEl} open={open} handleClose={handleClose}>
                <input
                    id="file-upload"
                    type="file"
                    style={{display: "none"}}
                    onChange={handleUploadFile}
                />
                <label htmlFor="file-upload">
                    <MenuItem component='span'>Upload</MenuItem>
                </label>
                {isIcon &&  <MenuItem onClick={handleDeleteFile}>Remove</MenuItem>}
            </DefaultMenu>
        </>

    );
};

export default AvatarMenu;