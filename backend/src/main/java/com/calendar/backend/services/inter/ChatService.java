package com.calendar.backend.services.inter;

import com.calendar.backend.models.Chat;

import java.util.List;
import java.util.Set;

public interface ChatService {
    Chat createPrivateChat(Long userId1, Long userId2);
    Chat createGroupChat(String title, Set<Long> userIds, int maxParticipants);
    void addParticipant(Chat chat, Long userId);
    boolean isUserInChat(Long chatId, Long userId);
    Chat findById(Long id);
    List<Chat> getChatsByUserId(Long userId);
}
