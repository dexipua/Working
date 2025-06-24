package com.calendar.backend.mappers;

import com.calendar.backend.dto.chat.ChatSimpleResponse;
import com.calendar.backend.models.Chat;
import com.calendar.backend.models.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatSimpleResponse toChatSimpleResponse(Chat chat);
}
