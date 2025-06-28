import axios from "axios";
import Cookies from 'js-cookie';

const API_URL = 'http://localhost:8081/'

class AuthService {

    static redirectToKeycloak() {
                   const keycloakUrl = "http://localhost:8080/realms/coffee-programmers/protocol/openid-connect/auth";
                   const clientId = "coffee-programmers-client";
                   const redirectUri = "http://localhost:3000/callback";
                   const loginUrl = `${keycloakUrl}?client_id=${clientId}` +
                       `&redirect_uri=${encodeURIComponent(redirectUri)}` +
                       `&response_type=code&scope=openid`;
                    window.location.href = loginUrl;
    };

    static async logout() {
        const userId = Cookies.get('userId');

        try {
            await axios.post('http://localhost:8081/api/auth/logout', {}, {
                params: {
                    userId: userId,
                },
                withCredentials: true,
            }).then(() => {
                AuthService.redirectToKeycloak();
            }).catch(error => {
                console.error('Logout failed:', error);
            });
        } catch (e) {
            console.error("Logout failed", e);
        }
    }

    static async refresh() {
        const refreshToken = Cookies.get('refreshToken');

        if (!refreshToken) {
            console.log('Refresh token not found or expired, redirecting to Keycloak');
            AuthService.redirectToKeycloak();
            return;
        }

        try {
            await axios.post('http://localhost:8081/api/auth/refresh', {}, {
                params: {
                    refreshToken: encodeURIComponent(refreshToken),
                },
                withCredentials: true,
            });
        } catch (error) {
            console.error('Refresh failed:', error);
            AuthService.redirectToKeycloak();
        }
    }


}

export default AuthService;
