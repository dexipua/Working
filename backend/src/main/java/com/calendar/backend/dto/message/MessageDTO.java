package com.calendar.backend.dto.message;

import com.calendar.backend.models.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long chatId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}
