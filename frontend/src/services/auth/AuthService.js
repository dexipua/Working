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
        const idToken = Cookies.get('idToken');

        try {
            await axios.post('http://localhost:8081/api/auth/logout', {}, {
                params: {
                    idToken: idToken,
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


}

export default AuthService;
