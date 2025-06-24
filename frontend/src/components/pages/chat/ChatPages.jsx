import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    TextField,
    Button,
    Checkbox,
    FormControlLabel,
    FormGroup,
    Paper,
    Divider,
    Alert,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';

import ChatService from '../../../services/base/ext/ChatService';
import UserService from '../../../services/base/ext/UserService';

const ChatsPage = () => {
    const [chats, setChats] = useState([]);
    const [users, setUsers] = useState([]); // Для вибору учасників групи
    const [selectedUserIds, setSelectedUserIds] = useState([]);
    const [chatTitle, setChatTitle] = useState('');
    const [isGroup, setIsGroup] = useState(false);
    const [error, setError] = useState(null);

    const navigate = useNavigate();
    const userId = Cookies.get('userId');

    useEffect(() => {
        // Завантажуємо чати користувача через сервіс
        ChatService.getMyChats()
            .then((res) => {
                console.log('Отримані чати:', res);
                setChats(res || []);
            })
            .catch(() => setError('Не вдалося завантажити чати'));

        // Завантажуємо користувачів (для груп) через сервіс
        UserService.getAllUsers(0, 10)
            .then((res) => {
                console.log('Отримані користувачі:', res);
                setUsers(res.content || res.data || []);
            })
            .catch(() => setError('Не вдалося завантажити користувачів'));
    }, []);

    useEffect(() => {
        console.log('Чати оновлено:', chats);
    }, [chats]);

    const toggleUser = (id) => {
        setSelectedUserIds((prev) =>
            prev.includes(id) ? prev.filter((uid) => uid !== id) : [...prev, id]
        );
    };

    const createChat = () => {
        setError(null);

        if (isGroup && (chatTitle.trim() === '' || selectedUserIds.length < 1)) {
            setError('Вкажіть назву групи та оберіть учасників');
            return;
        }

        if (!isGroup && selectedUserIds.length !== 1) {
            setError('Оберіть одного користувача для приватного чату');
            return;
        }

        if (isGroup) {
            ChatService.createGroupChat(chatTitle.trim(), [userId, ...selectedUserIds], 10)
                .then((res) => {
                    console.log('Створено груповий чат:', res.data);
                    // Якщо res.data — це об'єкт чату
                    setChats((prev) => [...prev, res.data]);
                    setChatTitle('');
                    setSelectedUserIds([]);
                })
                .catch(() => setError('Не вдалося створити чат'));
        } else {
            ChatService.createPrivateChat(userId, selectedUserIds[0])
                .then((res) => {
                    console.log('Створено приватний чат:', res.data);
                    setChats((prev) => [...prev, res.data]);
                    setSelectedUserIds([]);
                })
                .catch(() => setError('Не вдалося створити чат'));
        }
    };

    return (
        <Box maxWidth={600} mx="auto" mt={4}>
            <Typography variant="h4" mb={2}>
                Мої чати
            </Typography>

            {error && (
                <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Paper sx={{ mb: 4, p: 2 }}>
                <Typography variant="h6" mb={1}>
                    Створити чат
                </Typography>

                <FormControlLabel
                    control={<Checkbox checked={isGroup} onChange={(e) => setIsGroup(e.target.checked)} />}
                    label="Груповий чат"
                />

                {isGroup && (
                    <TextField
                        label="Назва групи"
                        value={chatTitle}
                        onChange={(e) => setChatTitle(e.target.value)}
                        fullWidth
                        margin="normal"
                    />
                )}

                <Typography variant="subtitle1" mt={2} mb={1}>
                    Виберіть учасників:
                </Typography>

                <FormGroup sx={{ maxHeight: 200, overflowY: 'auto', mb: 2 }}>
                    {users
                        .filter((u) => u.id !== userId)
                        .map((user) => (
                            <FormControlLabel
                                key={user.id}
                                control={
                                    <Checkbox
                                        checked={selectedUserIds.includes(user.id)}
                                        onChange={() => toggleUser(user.id)}
                                    />
                                }
                                label={user.username || `User ${user.id}`}
                            />
                        ))}
                </FormGroup>

                <Button variant="contained" onClick={createChat}>
                    Створити
                </Button>
            </Paper>

            <Typography variant="h6" mb={1}>
                Список чатів
            </Typography>

            {Array.isArray(chats) && chats.length > 0 ? (
                <List>
                    {chats.map((chat) => (
                        <React.Fragment key={chat.id}>
                            <ListItem disablePadding>
                                <ListItemButton onClick={() => navigate(`/chat/${chat.id}`)}>
                                    <ListItemText
                                        primary={`Чат #${chat.id}`}
                                        secondary={
                                            chat.isGroup
                                                ? `Група (${chat.participants.length} учасників)`
                                                : 'Приватний чат'
                                        }
                                    />
                                </ListItemButton>
                            </ListItem>
                            <Divider />
                        </React.Fragment>
                    ))}
                </List>
            ) : (
                <Typography variant="body2" color="textSecondary">
                    Немає доступних чатів.
                </Typography>
            )}
        </Box>
    );
};

export default ChatsPage;
