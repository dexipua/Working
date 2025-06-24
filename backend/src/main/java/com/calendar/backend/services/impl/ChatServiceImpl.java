package com.calendar.backend.services.impl;

import com.calendar.backend.models.Chat;
import com.calendar.backend.models.ChatParticipant;
import com.calendar.backend.models.User;
import com.calendar.backend.repositories.ChatParticipantRepository;
import com.calendar.backend.repositories.ChatRepository;
import com.calendar.backend.services.inter.ChatService;
import com.calendar.backend.services.inter.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final ChatParticipantRepository participantRepository;

    public Chat createPrivateChat(Long userId1, Long userId2) {
        log.info("Service: creating private chat");
        log.info("Service: checking for existence of chat");
        Optional<Chat> existing = chatRepository.findPrivateChatBetweenUsers(userId1, userId2);
        if (existing.isPresent()) return existing.get();
        log.info("Service: creating new private chat");
        Chat chat = new Chat();
        chat.setGroup(false);
        chat = chatRepository.save(chat);

        addParticipant(chat, userId1);
        addParticipant(chat, userId2);

        return chat;
    }

    public Chat createGroupChat(String title, Set<Long> userIds, int maxParticipants) {
        log.info("Service: creating group chat");
        if (userIds.size() > maxParticipants) {
            log.info("Service: creating group chat with more than {} users", maxParticipants);
            throw new IllegalArgumentException("Too many participants");
        }
        log.info("Service: creating group chat after checking");
        Chat chat = new Chat();
        chat.setTitle(title);
        chat.setGroup(true);
        chat = chatRepository.save(chat);

        for (Long userId : userIds) {
            addParticipant(chat, userId);
        }

        return chat;
    }

    public void addParticipant(Chat chat, Long userId) {
        log.info("Service: adding participant to chat");
        User user = userService.findById(userId);
        ChatParticipant cp = new ChatParticipant();
        cp.setChat(chat);
        cp.setUser(user);
        cp.setJoinedAt(LocalDateTime.now());
        participantRepository.save(cp);
    }

    public boolean isUserInChat(Long chatId, Long userId) {
        log.info("Service: checking if chat exists in chat");
        return participantRepository.existsByChatIdAndUserId(chatId, userId);
    }

    @Override
    public Chat findById(Long id) {
        log.info("Service: finding chat with id {}", id);
        return chatRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Chat not found"));
    }

    @Override
    public List<Chat> getChatsByUserId(Long userId) {
        return chatRepository.findAllByParticipantUserId(userId);
    }
}
