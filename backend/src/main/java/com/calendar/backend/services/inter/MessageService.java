package com.calendar.backend.services.inter;

import com.calendar.backend.models.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Long chatId, Long senderId, String content);
    List<Message> getMessages(Long chatId);
}
