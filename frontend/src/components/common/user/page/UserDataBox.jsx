import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import EmailIcon from "@mui/icons-material/Email";
import PersonIcon from "@mui/icons-material/Person";
import {Box, Button} from "@mui/material";
import Typography from "@mui/material/Typography";
import React, {useEffect, useState} from "react";
import InfoList from "../../../layouts/InfoList";
import TextUtils from "../../../../utils/TextUtils";
import DateUtils from "../../../../utils/DateUtils";
import FileService from "../../../../services/base/ext/FileService";
import {useError} from "../../../../contexts/ErrorContext";
import FileUtils from "../../../../utils/FileUtils";
import Loading from "../../../layouts/Loading";

const UserDataBox = ({user}) => {
    const {showError} = useError();
    
    const [status, setStatus] = useState()
    const [profileIcon, setProfileIcon] = useState(null)

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    useEffect(() => {
        const fetchData = async () => {
            try {
                console.log("1")
                const response = await FileService.getAvatar(user.id);
                console.log("2")

                console.log(response)
                setProfileIcon(response || null);
            } catch (error) {
                console.log("3")
                console.log(error)
                setError(error);
            } finally {
                console.log("4")

                setLoading(false);
            }
        }

        fetchData();
    }, [user.id]);
    
    const optionList = [
        {
            icon: <CalendarMonthIcon/>,
            label: "Birthday:",
            value: DateUtils.formatBirthdayDate(user.birthday)
        },
        {
            icon: <EmailIcon/>,
            label: "Email:",
            value: user.email
        },
        {
            icon: <PersonIcon/>,
            label: "Role:",
            value: TextUtils.formatEnumText(user.role)
        },
    ]

    const handleFileChange = async (e) => {
        console.log('handleFileChange')
        await uploadFile(e.target.files[0])
        e.target.value = null;
    };

    const uploadFile = async (file) => {
        if (!file) return;
        
        setStatus("Обчислення хешу...");
        const hash = await FileUtils.computeHash(file);

        setStatus("Завантаження файлу...");
        try {
            const response = await FileService.uploadAvatar(file, hash, 1);
            setProfileIcon(response);
            setStatus(" ");
        } catch (err) {
            setStatus("Помилка при завантаженні");
        }
    };

    if (loading) {
        return <Loading/>;
    }

    if (error) {
        return <Typography color={"error"}>Error: {error.message}</Typography>;
    }

    return (
        <Box sx={{
            border: '1px solid #ddd',
            padding: '15px',
            borderRadius: "10px",
        }}>
            <Box sx={{
                paddingX: '20px',
                display: 'flex', flexDirection: 'column', alignItems: 'center'
            }}>
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
                }}>
                    {profileIcon === null ? (
                        <PersonIcon color="primary" sx={{fontSize: 250, padding: 0, margin: 0}}/>
                    ) : (
                        profileIcon.fileType.startsWith("image/") && (
                            <Box
                                component="img"
                                src={profileIcon.path}
                                alt={profileIcon.fileRealName}
                                sx={{
                                    width: "101%",
                                    height: "101%",
                                    borderRadius: "50%",
                                    objectFit: "cover"
                                }}
                            />
                        )
                    )}

                </Box>

                <Box>
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
                </Box>

                <Box mb={2} sx={{display: 'flex', alignItems: 'center',}}>
                    <Typography fontWeight={'bold'} variant="h4">{TextUtils.getUserFullName(user)}</Typography>
                </Box>

                <Box>
                    <Box sx={{display: 'flex', flexDirection: 'column',}}>
                        <Typography sx={{fontWeight: "bold", textAlign: 'center'}}>
                            About me:
                        </Typography>
                        <InfoList list={optionList}/>
                    </Box>

                    <Box sx={{display: 'flex', flexDirection: 'column'}}>
                        <Typography mt={2} sx={{fontWeight: "bold", textAlign: 'center'}}>
                            Description:
                        </Typography>
                        <Typography mt={0.5}>{user.description} Lorem ipsum dolor sit amet, consectetur adipisicing
                            elit. Aperiam commodi facilis maiores quae quibusdam rerum voluptatem. Accusamus, alias
                            aliquid animi consequuntur dicta dignissimos incidunt, ipsam maxime nihil officiis quaerat
                            quas.</Typography>
                    </Box>

                </Box>
            </Box>
        </Box>
    )
}

export default UserDataBox;