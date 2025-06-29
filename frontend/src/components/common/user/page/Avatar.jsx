import React, {useEffect, useState} from 'react';
import Box from "@mui/material/Box";
import PersonIcon from "@mui/icons-material/Person";
import {Fade} from "@mui/material";
import {grey} from "@mui/material/colors";
import AvatarMenu from "./AvatarMenu";
import FileService from "../../../../services/base/ext/FileService";
import FileUtils from "../../../../utils/FileUtils";
import Loading from "../../../layouts/Loading";

const Avatar = ({userId}) => {
    const [profileIcon, setProfileIcon] = useState(null)
    const [hover, setHover] = useState(false);

    const isIcon = profileIcon !== null;

    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await FileService.getAvatar(userId);
                setProfileIcon(response || null);
            } catch (error) {
                console.log(error);
            } finally {
                setLoading(false)
            }
        }

        fetchData();
    }, [userId]);

    const uploadProfileIcon = async (file) => {
        setLoading(true);
        const hash = await FileUtils.computeHash(file);
        const response = await FileService.uploadAvatar(file, hash, userId)
        setProfileIcon(response || null);
        setLoading(false);

    }

    const deleteProfileIcon = async () => {
        setLoading(true)
        await FileService.deleteFile(profileIcon.id);
        setProfileIcon(null);
        setLoading(false);

    }

    const renderProfileIcon = () => {
        if (!profileIcon) {
            return (
                <PersonIcon
                    color="primary"
                    sx={{fontSize: 250, padding: 0, margin: 0, borderRadius: "50%"}}
                />
            );
        }

        if (profileIcon.fileType.startsWith("image/")) {
            return (
                <Box
                    component="img"
                    src={profileIcon.path}
                    alt={profileIcon.fileRealName}
                    sx={{
                        width: "101%",
                        height: "101%",
                        borderRadius: "50%",
                        objectFit: "cover",
                    }}
                />
            );
        }

        return null;
    };

    return (
        <Box sx={{
            height: 250,
            width: 250,
            padding: 0,
            marginBottom: "10px",
            borderRadius: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            border: 3,
            borderColor: 'primary.dark'
        }}
             onMouseLeave={() => setHover(false)}
             onMouseEnter={() => setHover(true)}
        >
            {loading ? (
                <Loading/>
            ) : (hover ? (
                    <Fade in={hover} timeout={600}>
                        <Box
                            sx={{
                                width: "100%",
                                height: "100%",
                                borderRadius: "50%",
                                objectFit: "cover",
                                backgroundColor: grey[200],
                                display: "flex",
                                justifyContent: "center",
                                alignItems: "center",
                            }}>
                            <AvatarMenu
                                uploadProfileIcon={uploadProfileIcon}
                                deleteProfileIcon={deleteProfileIcon}
                                isIcon={isIcon}
                                setHover={setHover}
                            />
                        </Box>
                    </Fade>
                ) : (
                    renderProfileIcon()
                )
            )}

        </Box>
    );
};

export default Avatar;