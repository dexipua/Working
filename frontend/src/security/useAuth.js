// import Cookies from 'js-cookie';

import Cookies from "js-cookie";

export const useAuth = () => {
    const isAuthenticated = () => {
        return Cookies.get('userId') !== undefined;
    };

    return { isAuthenticated};
};
