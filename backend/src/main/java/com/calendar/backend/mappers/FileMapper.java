package com.calendar.backend.mappers;

import com.calendar.backend.dto.file.FileSimpleResponse;
import com.calendar.backend.models.File;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileSimpleResponse toSimpleResponse(File file);
}
