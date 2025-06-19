import React, {useEffect, useState} from 'react';
import Loading from "../../../../layouts/Loading";
import {Typography} from "@mui/material";
import UserView from "../UserView";
import UserService from "../../../../../services/base/ext/UserService";
import Cookies from "js-cookie";
import {useError} from "../../../../../contexts/ErrorContext";

const ProfileContent = () => {
    const {showError} = useError();
    const myId = Cookies.get('userId');
    const [user, setUser] = useState(null);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [visitedEventsCount, setVisitedEventsCount] = useState(0);
    const [completedTasksCount, setCompletedTasksCount] = useState({countCompleted:0, countAll: 0});
    const [writtenCommentsCount, setWrittenCommentsCount] = useState(0);
    

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response1 = await UserService.getMyUser();

                setUser(response1);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [myId]);

    const handleUpdate = async (updatedUser) => {
        try {
            const response = await UserService.updateMyUser(updatedUser);
            setUser(response);
        } catch (error) {
            showError(error);

        }
    }
    
    const handleUpdatePassword = async (password, newPassword) => {
        try {
            await UserService.updateMyPassword(password, newPassword);
        } catch (error) {
            showError(error);
        }
        
    }
    
    if (loading) {
        return <Loading/>;
    }

    if (error) {
        return <Typography color={"error"}>Error: {error.message}</Typography>;
    }

    return (
        <UserView
            handleUpdate={handleUpdate}
            user={user}
            handleUpdatePassword={handleUpdatePassword}
        />
    );
};

export default ProfileContent;