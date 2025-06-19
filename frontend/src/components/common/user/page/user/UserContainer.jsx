import React, {useEffect, useState} from 'react';
import Loading from "../../../../layouts/Loading";
import {Typography} from "@mui/material";
import UserView from "../UserView";
import UserService from "../../../../../services/base/ext/UserService";
import {useParams} from "react-router-dom";
import {useError} from "../../../../../contexts/ErrorContext";

const UserContainer = () => {
    const {showError} = useError();
    const {id} = useParams();

    const [user, setUser] = useState(null);

    const [visitedEventsCount, setVisitedEventsCount] = useState(0);
    const [completedTasksCount, setCompletedTasksCount] = useState({countCompleted:0, countAll: 0});
    const [writtenCommentsCount, setWrittenCommentsCount] = useState(0);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response1 = await UserService.getUser(id);

                setUser(response1);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [id]);

    const handleUpdate = async (data) => {
        try {
            const response = await UserService.updateUser(user.id, data);
            setUser(response);
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
            visitedEventsCount={visitedEventsCount}
            completedTasksCount={completedTasksCount}
            writtenCommentsCount={writtenCommentsCount}
        />
    );
};

export default UserContainer;