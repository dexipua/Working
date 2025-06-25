package com.calendar.backend.controllers;


import com.calendar.backend.dto.chat.ChatSimpleResponse;
import com.calendar.backend.dto.chat.CreateGroupChatRequest;
import com.calendar.backend.dto.chat.CreatePrivateChatRequest;
import com.calendar.backend.mappers.ChatMapper;
import com.calendar.backend.models.Chat;
import com.calendar.backend.services.inter.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatMapper chatMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/private")
    public ChatSimpleResponse createPrivateChat(@RequestBody CreatePrivateChatRequest request) {
        log.info("Controller: Received request to Create Private Chat");
        Chat chat = chatService.createPrivateChat(request.getUserId1(), request.getUserId2());
        return chatMapper.toChatSimpleResponse(chat);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/group")
    public ChatSimpleResponse createGroupChat(@RequestBody CreateGroupChatRequest request) {
        log.info("Controller: Received request to Create Group Chat");
        Chat chat = chatService.createGroupChat(
                request.getTitle(),
                new HashSet<>(request.getUserIds()),
                request.getMaxParticipants()
        );
        return chatMapper.toChatSimpleResponse(chat);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/my")
    public List<ChatSimpleResponse> getMyChats(@AuthenticationPrincipal(expression = "id") Long currentUserId) {
        log.info("Controller: Received request to Get My Chats");
        List<ChatSimpleResponse> chats = chatService.getChatsByUserId(currentUserId).stream()
                .map(chatMapper::toChatSimpleResponse).toList();
        return chats;
    }
}
