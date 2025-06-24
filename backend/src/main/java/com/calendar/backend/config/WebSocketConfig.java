package com.calendar.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Вхідні повідомлення — клієнт слухає через /topic
        registry.enableSimpleBroker("/topic");

        // Вихідні повідомлення — клієнт відправляє на /app/...
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Основна точка входу для WebSocket-клієнтів
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // або твій домен
                .withSockJS(); // дозволяє fallback через HTTP
    }
}
