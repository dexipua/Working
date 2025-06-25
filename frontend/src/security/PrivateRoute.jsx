import React, { useEffect } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './useAuth';
import Cookies from "js-cookie";

const redirectToKeycloak = () => {
    const keycloakUrl = "http://localhost:8080/realms/coffee-programmers/protocol/openid-connect/auth";
    const clientId = "coffee-programmers-client";
    const redirectUri = "http://localhost:3000/callback";
    const loginUrl = `${keycloakUrl}?client_id=${clientId}` +
        `&redirect_uri=${encodeURIComponent(redirectUri)}` +
        `&response_type=code&scope=openid`;

    window.location.href = loginUrl;
};

const PrivateRoute = () => {
    const { isAuthenticated } = useAuth();
    const location = useLocation();

    if (location.pathname === "/callback") {
        return <Outlet />;
    }

    return isAuthenticated() ? <Outlet /> : redirectToKeycloak();
};

export default PrivateRoute;
