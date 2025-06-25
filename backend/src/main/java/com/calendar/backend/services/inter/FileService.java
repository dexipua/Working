package com.calendar.backend.services.inter;

import com.calendar.backend.dto.file.FileSimpleResponse;
import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.models.File;
import com.calendar.backend.models.enums.FileType;
import kong.unirest.FileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface FileService {
    String save(MultipartFile file, long ownerId, String frontendHash) throws IOException, NoSuchAlgorithmException;
    void delete(long id);
    PaginationListResponse<FileSimpleResponse> findByUserId(Long userId, int page, int size);
    boolean existsByFileHashAndUser_Id(String fileHash, Long userId);
}
