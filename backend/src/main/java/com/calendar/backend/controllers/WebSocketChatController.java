package com.calendar.backend.controllers;

import com.calendar.backend.dto.message.MessageDTO;
import com.calendar.backend.mappers.MessageMapper;
import com.calendar.backend.models.Message;
import com.calendar.backend.services.inter.ChatService;
import com.calendar.backend.services.inter.MessageService;
import com.calendar.backend.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper messageMapper;
    private final UserService userService;

    @MessageMapping("/chat/{chatId}")
    public void handleMessage(@DestinationVariable Long chatId,
                              @Payload MessageDTO messageDto,
                              Principal principal) {

        Long senderId = userService.findUserByEmail(principal.getName()).getId();
        if (!chatService.isUserInChat(chatId, senderId)) {
            throw new AccessDeniedException("You are not in this chat");
        }

        Message savedMessage = messageService.sendMessage(chatId, senderId, messageDto.getContent());

        messagingTemplate.convertAndSend("/topic/chat/" + chatId, messageMapper.fromModelToDto(savedMessage));
    }
}
