import {BrowserRouter, Route, Routes, useNavigate} from 'react-router-dom';
import {ThemeProvider, Typography} from '@mui/material';
import {LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';

import theme from './assets/theme';

import Page from './components/layouts/Page';
import Users from './components/pages/user/Users';
import UserPage from './components/pages/user/UserPage';
import ProfilePage from './components/pages/user/ProfilePage';
import {history} from "./utils/history";
import {useEffect} from "react";

import './index.css';
import PrivateRoute from "./security/PrivateRoute";
import {ErrorProvider} from "./contexts/ErrorContext";
import FilePage from "./components/pages/file/FilePage";
import ChatsPage from "./components/pages/chat/ChatPages";
import Chat from "./components/pages/chat/Chat"
import Callback from "./security/callback/Callback";
import TeacherPanelPage from "./components/pages/teacher_panel/TeacherPanelPage";

import Cookies from "js-cookie";

const InitNavigation = ({children}) => {
    const navigate = useNavigate();

    useEffect(() => {
        history.navigate = navigate;
    }, [navigate]);

    return children;
};

function App() {

    const role = Cookies.get('role');

    const routes = [
        {path: "/callback", element: <Callback/>},
        {path: "/users", element: <Page><Users/></Page>},
        {path: "/profile", element: <ProfilePage/>},
        {path: "/users/:id", element: <Page><UserPage/></Page>},
        {path: "/files/:userId", element: <Page><FilePage/></Page>},
        {path: "/chats", element: <Page><ChatsPage/></Page> },
        {path: "/chat/:chatId", element: <Page><Chat/></Page> },
        role === 'TEACHER' && {path: "/teacherPanel", element: <TeacherPanelPage/>},
        {
            path: "*", element:
                <Page>
                    <Typography p={"50px"} variant={"h4"} color={'error'}>
                        404 Page not found
                    </Typography>
                </Page>
        }

    ];

    return (
        <BrowserRouter>
            <InitNavigation>
                <ErrorProvider>
                    <ThemeProvider theme={theme}>
                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                            <Routes>
                                {routes.map((route, index) => (
                                    <Route element={<PrivateRoute/>} key={index}>
                                        <Route
                                            path={route.path}
                                            element={route.element}
                                        />
                                    </Route>
                                ))}
                            </Routes>
                        </LocalizationProvider>
                    </ThemeProvider>
                </ErrorProvider>
            </InitNavigation>
        </BrowserRouter>
    );
}

export default App;
