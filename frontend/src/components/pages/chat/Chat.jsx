import React, { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import Cookies from 'js-cookie';
import { useParams } from 'react-router-dom';

import { Box, TextField, Button, Typography, Paper } from '@mui/material';

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const { chatId } = useParams();
    const userId = Cookies.get('userId');
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8081/ws');

        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            debug: (str) => {
                console.log('[STOMP]', str);
            },
            onConnect: () => {
                console.log('Connected to WebSocket');

                client.subscribe(`/topic/chat/${chatId}`, (msg) => {
                    const message = JSON.parse(msg.body);
                    setMessages((prev) => [...prev, message]);
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },
        });

        client.activate();
        stompClient.current = client;

        return () => {
            client.deactivate();
        };
    }, [chatId]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const sendMessage = () => {
        if (input.trim() === '' || !stompClient.current?.connected) return;

        const message = {
            chatId,
            senderId: userId,
            content: input,
        };

        stompClient.current.publish({
            destination: `/app/chat/${chatId}`,
            body: JSON.stringify(message),
        });

        setInput('');
    };

    return (
        <Paper
            elevation={3}
            sx={{
                maxWidth: 500,
                margin: '20px auto',
                padding: 2,
                display: 'flex',
                flexDirection: 'column',
                height: 400,
            }}
        >
            <Typography variant="h6" mb={2} align="center">
                Chat Room #{chatId}
            </Typography>

            <Box
                sx={{
                    flexGrow: 1,
                    overflowY: 'auto',
                    border: '1px solid',
                    borderColor: 'divider',
                    borderRadius: 1,
                    padding: 2,
                    mb: 2,
                    bgcolor: 'background.paper',
                }}
            >
                {messages.map((m, i) => (
                    <Box
                        key={i}
                        sx={{
                            display: 'flex',
                            justifyContent: m.senderId === userId ? 'flex-end' : 'flex-start',
                            mb: 1,
                        }}
                    >
                        <Box
                            sx={{
                                bgcolor: m.senderId === userId ? 'primary.main' : 'grey.300',
                                color: m.senderId === userId ? 'primary.contrastText' : 'text.primary',
                                px: 2,
                                py: 1,
                                borderRadius: 2,
                                maxWidth: '70%',
                                wordWrap: 'break-word',
                            }}
                        >
                            <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                                {m.senderId === userId ? 'You' : `User ${m.senderId}`}
                            </Typography>
                            <Typography variant="body1">{m.content}</Typography>
                        </Box>
                    </Box>
                ))}
                <div ref={messagesEndRef} />
            </Box>

            <Box sx={{ display: 'flex', gap: 1 }}>
                <TextField
                    fullWidth
                    variant="outlined"
                    placeholder="Напиши повідомлення..."
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
                    size="small"
                />
                <Button variant="contained" onClick={sendMessage}>
                    Відправити
                </Button>
            </Box>
        </Paper>
    );
};

export default Chat;
