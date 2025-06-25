package com.calendar.backend.controllers;

import com.calendar.backend.dto.file.FileSimpleResponse;
import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.models.File;
import com.calendar.backend.services.inter.FileService;
import com.calendar.backend.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> upload(
            @RequestParam MultipartFile file,
            @RequestParam long ownerId,
            @RequestParam String frontendHash) throws IOException, NoSuchAlgorithmException {
        log.info("Controller: upload file");
        String url = fileService.save(file, ownerId, frontendHash);
        return ResponseEntity.ok(url);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PaginationListResponse<FileSimpleResponse> getAllFiles(
            @RequestParam Long userId,
            @RequestParam int page,
            @RequestParam int size) {
        log.info("Controller: get files with userId: {}", userId);
        return fileService.findByUserId(userId, page, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Controller: delete file with id: {}", id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
