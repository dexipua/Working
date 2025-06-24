package com.calendar.backend.dto.chat;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupChatRequest {
    private String title;
    private List<Long> userIds;
    private Integer maxParticipants;
}