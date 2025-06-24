package com.calendar.backend.dto.chat;

import lombok.Data;

@Data
public class ChatSimpleResponse {
    private final long id;
    private final String title;
    private final boolean isGroup;
}
