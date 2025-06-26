import {BrowserRouter, Route, Routes, useNavigate} from 'react-router-dom';
import {ThemeProvider} from '@mui/material';
import {LocalizationProvider} from '@mui/x-date-pickers';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import theme from './assets/theme';
import Callback from './security/callback/Callback';
import Users from './components/pages/user/Users';
import UserPage from './components/pages/user/UserPage';
import Profile from './components/pages/user/Profile';
import {history} from "./utils/history";
import {useEffect} from "react";
import PrivateRoute from "./security/PrivateRoute";
import {ErrorProvider} from "./contexts/ErrorContext";
import FilePage from "./components/pages/file/FilePage";
import ChatsPage from "./components/pages/chat/ChatPages";
import Chat from "./components/pages/chat/Chat"
import TeacherPanelPage from "./components/pages/teacher_panel/TeacherPanelPage"
import PageContainer from "./components/layouts/appbar_with_drawer/PageContainer";
import First from "./components/pages/template/First";
import Second from "./components/pages/template/Second";
import Third from "./components/pages/template/Third";
import NotFoundPage from "./components/pages/not_found_page/NotFoundPage";

const InitNavigation = ({children}) => {
    const navigate = useNavigate();

    useEffect(() => {
        history.navigate = navigate;
    }, [navigate]);

    return children;
};

function App() {

    const routes = [
        {path: "/first", element: <First/>},
        {path: "/second", element: <Second/>},
        {path: "/third", element: <Third/>},

        {path: "/teacher-panel", element: <TeacherPanelPage/>},
        {path: "/callback", element: <Callback/>},
        {path: "/users", element: <Users/>},
        {path: "/profile", element: <Profile/>},
        {path: "/users/:id", element: <UserPage/>},
        {path: "/files/:userId", element: <FilePage/>},
        {path: "/chats", element: <ChatsPage/>},
        {path: "/chat/:chatId", element: <Chat/>},

        {path: "*", element:<NotFoundPage/>},

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
                                            element={<PageContainer>{route.element}</PageContainer>}
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
