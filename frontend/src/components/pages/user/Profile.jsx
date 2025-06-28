import React, {useEffect, useState} from 'react';
import {useError} from "../../../contexts/ErrorContext";
import UserService from "../../../services/base/ext/UserService";
import Loading from "../../layouts/Loading";
import {Typography} from "@mui/material";
import UserDataBox from "../../common/user/page/UserDataBox";
import Box from "@mui/material/Box";
import Statistics from "../../common/user/page/Statistics";

const Profile = () => {
    const {showError} = useError();
    const [user, setUser] = useState(null);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await UserService.getMyUser();
                setUser(response);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return <Loading/>;
    }

    if (error) {
        return <Typography color={"error"}>Error: {error.message}</Typography>;
    }

    return (
        <Box sx={{display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%',}}>
            <Box sx={{
                width: '100%',
                display: "flex",
                flexDirection: "column"
            }}>
                <Box sx={{display: 'grid', gridTemplateColumns: '1.2fr 2.5fr', gap: '15px'}}>
                    <UserDataBox user={user}/>
                    <Statistics/>
                </Box>

            </Box>
        </Box>
    );
};

export default Profile;