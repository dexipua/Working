import axios from "axios";
import Cookies from "js-cookie";
import {history} from "./history";

export const client = axios.create({
    baseURL: "http://localhost:8081/api",
    withCredentials: true,
});

client.interceptors.request.use(
    config => {
        const token = Cookies.get("accessToken");
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

const redirectToKeycloak = () => {
    const keycloakUrl = "http://localhost:8080/realms/coffee-programmers/protocol/openid-connect/auth";
    const clientId = "coffee-programmers-client";
    const redirectUri = "http://localhost:3000/callback";
    const loginUrl = `${keycloakUrl}?client_id=${clientId}` +
        `&redirect_uri=${encodeURIComponent(redirectUri)}` +
        `&response_type=code&scope=openid`;

    window.location.href = loginUrl;
};

client.interceptors.response.use(
    response => response,
    async error => {
        const { response, config } = error;
        console.log(
            "Intercepted error",
            error,
            response,
            config,
            config._retried)
        if (response && response.status === 498 && !config._retried) {
            config._retried = true;
            return client(config);
        }

        if (response && response.status === 401) {
            Cookies.remove("accessToken");
            if (history.navigate) {
                redirectToKeycloak();
            }
        }
        return Promise.reject(error);
    }
);
