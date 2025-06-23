import axios from "axios";
import Cookies from 'js-cookie';

const API_URL = 'http://localhost:8081/api/'

class AuthService {

    static async login(email, password) {
        try {
            const
                response = await axios.post(`${API_URL}auth/login`, {
                    username: email,
                    password,
                }, {
                    withCredentials: true
                });
            console.log(response.data)
        } catch (error) {
            throw error;
        }
        console.log(Cookies.get('jwtToken'));
    }

    static async logout() {
        try {
            await axios.post(`${API_URL}auth/logout`, {}, {
                withCredentials: true,
            });

        } catch (error) {
            console.error("Logout failed:", error);
            throw error;
        }
    }
}

export default AuthService;
