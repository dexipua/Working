import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
const Callback = () => {
    const navigate = useNavigate();

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');
        console.log("HERE")
        if (code) {
            fetch('http://localhost:8081/api/auth/callback', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({ code }),
                credentials: 'include' // щоб кука з токеном зберіглась
            }).then(res => {
                if (res.ok) {
                    navigate('/');
                } else {
                    alert('Помилка авторизації');
                }
            });
        }
    }, [navigate]);

    return <p>Авторизація…</p>;
};

export default Callback;
