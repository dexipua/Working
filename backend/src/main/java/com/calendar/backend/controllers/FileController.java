package com.calendar.backend.controllers;

import com.calendar.backend.dto.file.FileSimpleResponse;
import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.mappers.FileMapper;
import com.calendar.backend.models.File;
import com.calendar.backend.models.enums.FileType;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileMapper fileMapper;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public FileSimpleResponse upload(
            @RequestParam MultipartFile file,
            @RequestParam long ownerId,
            @RequestParam String frontendHash) throws IOException, NoSuchAlgorithmException {
        log.info("Controller: upload file");
        return fileMapper.toSimpleResponse(fileService.save(file, ownerId, frontendHash, FileType.OTHER));
    }

    @PostMapping("/upload/avatar")
    @ResponseStatus(HttpStatus.CREATED)
    public FileSimpleResponse uploadAvatar(
            @RequestParam MultipartFile file,
            @RequestParam long ownerId,
            @RequestParam String frontendHash) throws IOException, NoSuchAlgorithmException {
        log.info("Controller: upload avatar file");
        return fileMapper.toSimpleResponse(fileService.save(file, ownerId, frontendHash, FileType.AVATAR));
    }

    @GetMapping("/check/avatar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> checkAvatar(
            @RequestParam Long userId){
        log.info("Controller: check avatar file");
        return ResponseEntity.ok(fileService.haveAvatar(userId));
    }

    @GetMapping("/avatar")
    @ResponseStatus(HttpStatus.OK)
    public FileSimpleResponse getAvatar(
            @RequestParam Long userId){
        log.info("Controller: get avatar");
        return fileMapper.toSimpleResponse(fileService.getAvatar(userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PaginationListResponse<FileSimpleResponse> getAllFiles(
            @RequestParam Long userId,
            @RequestParam int page,
            @RequestParam int size) {
        log.info("Controller: get files with userId: {}", userId);
        PaginationListResponse<File>  list = fileService.findByUserId(userId, page, size);
        PaginationListResponse<FileSimpleResponse> response = new PaginationListResponse<>();
        response.setTotalPages(list.getTotalPages());
        response.setContent(list.getContent().stream().map(fileMapper::toSimpleResponse).collect(Collectors.toList()));
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Controller: delete file with id: {}", id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
