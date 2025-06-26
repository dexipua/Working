import axios from "axios";
import Cookies from 'js-cookie';

const API_URL = 'http://localhost:8081/'

class AuthService {

    static async logout() {
        const accessToken = Cookies.get('accessToken');
        const idToken = Cookies.get('idToken');

        try {
            await axios.post('http://localhost:8081/api/auth/logout', {}, {
                withCredentials: true,
            });

            const postLogoutRedirectUri = "http://localhost:3000/";
            const logoutUrl =
                `http://localhost:8080/realms/coffee-programmers/protocol/openid-connect/logout` +
                `?post_logout_redirect_uri=${encodeURIComponent(postLogoutRedirectUri)}` +
                `&id_token_hint=${idToken}`;

            window.location.href = logoutUrl;
        } catch (e) {
            console.error("Logout failed", e);
        }
    }


}

export default AuthService;
