package com.calendar.backend.dto.file;

import lombok.Data;

@Data
public class FileSimpleResponse {
    private long id;
    private String fileRealName;
    private String fileType;
    private String path;
}
