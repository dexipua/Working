package com.calendar.backend.dto.chat;

import lombok.Data;

@Data
public class CreatePrivateChatRequest {
    private Long userId1;
    private Long userId2;
}