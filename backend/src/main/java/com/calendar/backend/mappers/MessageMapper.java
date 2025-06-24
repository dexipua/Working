package com.calendar.backend.mappers;

import com.calendar.backend.dto.message.MessageDTO;
import com.calendar.backend.models.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageDTO fromModelToDto(Message message);
}
