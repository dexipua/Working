import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import EmailIcon from "@mui/icons-material/Email";
import PersonIcon from "@mui/icons-material/Person";
import {Box} from "@mui/material";
import Typography from "@mui/material/Typography";
import React, {useState} from "react";
import InfoList from "../../../layouts/InfoList";
import TextUtils from "../../../../utils/TextUtils";
import DateUtils from "../../../../utils/DateUtils";
import FileUploader from "../../file/FileUploader";

const UserDataBox = ({user}) => {
    const [profileIcon, setProfileIcon] = useState(null)
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

    const onUploadProfileIcon = (file) => {
      setProfileIcon(file)
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
                                    // sx={{width: "100%", height: 160, , borderRadius: 2, mb: 1}}
                                />
                            )
                    )}

                </Box>
                <FileUploader onUploadFile={onUploadProfileIcon}/>

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
                        <Typography mt={0.5}>{user.description} Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aperiam commodi facilis maiores quae quibusdam rerum voluptatem. Accusamus, alias aliquid animi consequuntur dicta dignissimos incidunt, ipsam maxime nihil officiis quaerat quas.</Typography>
                    </Box>

                </Box>
            </Box>
        </Box>
    )
}

export default UserDataBox;