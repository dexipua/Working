package com.calendar.backend.services.impl;

import com.calendar.backend.models.Chat;
import com.calendar.backend.models.Message;
import com.calendar.backend.models.User;
import com.calendar.backend.repositories.MessageRepository;
import com.calendar.backend.repositories.UserRepository;
import com.calendar.backend.services.inter.ChatService;
import com.calendar.backend.services.inter.MessageService;
import com.calendar.backend.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final UserService userService;

    public Message sendMessage(Long chatId, Long senderId, String content) {
        log.info("Service: sending message to chat with id {}", chatId);
        Chat chat = chatService.findById(chatId);

        User sender = userService.findById(senderId);

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getMessages(Long chatId) {
        log.info("Service: getting messages for chat with id {}", chatId);
        return messageRepository.findByChatIdOrderBySentAtAsc(chatId);
    }
}
