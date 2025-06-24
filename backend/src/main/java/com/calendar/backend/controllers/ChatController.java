package com.calendar.backend.controllers;


import com.calendar.backend.dto.chat.ChatSimpleResponse;
import com.calendar.backend.dto.chat.CreateGroupChatRequest;
import com.calendar.backend.dto.chat.CreatePrivateChatRequest;
import com.calendar.backend.mappers.ChatMapper;
import com.calendar.backend.models.Chat;
import com.calendar.backend.services.inter.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper chatMapper;

    // Створити приватний чат між двома користувачами
    @PostMapping("/private")
    public ResponseEntity<Chat> createPrivateChat(@RequestBody CreatePrivateChatRequest request,
                                                  @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        // currentUserId можна отримати із токена, якщо використовуєш Spring Security

        // Перевірка, що currentUserId співпадає з userId1 (або зроби додаткову логіку)
        Chat chat = chatService.createPrivateChat(request.getUserId1(), request.getUserId2());
        return ResponseEntity.ok(chat);
    }

    // Створити груповий чат
    @PostMapping("/group")
    public ResponseEntity<Chat> createGroupChat(@RequestBody CreateGroupChatRequest request,
                                                @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        Chat chat = chatService.createGroupChat(
                request.getTitle(),
                new HashSet<>(request.getUserIds()),
                request.getMaxParticipants()
        );
        return ResponseEntity.ok(chat);
    }

    // Отримати всі чати поточного користувача
    @GetMapping("/my")
    public ResponseEntity<List<ChatSimpleResponse>> getMyChats(@AuthenticationPrincipal(expression = "id") Long currentUserId) {
        List<ChatSimpleResponse> chats = chatService.getChatsByUserId(currentUserId).stream().map(chatMapper::toChatSimpleResponse).toList();
        return ResponseEntity.ok(chats);
    }
}
