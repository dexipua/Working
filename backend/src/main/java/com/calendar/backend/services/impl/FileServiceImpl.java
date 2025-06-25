package com.calendar.backend.services.impl;

import com.calendar.backend.dto.file.FileSimpleResponse;
import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.mappers.FileMapper;
import com.calendar.backend.models.File;
import com.calendar.backend.repositories.FileRepository;
import com.calendar.backend.services.inter.FileService;
import com.calendar.backend.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserService userService;
    private final SupabaseStorageService supabaseStorageService;
    private final FileMapper fileMapper;

    @Override
    public String save(MultipartFile file, long ownerId, String frontendHash) throws IOException, NoSuchAlgorithmException {
        log.info("Service: Save file with id {}", file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        String computedHash = sha256(bytes);

        if (!computedHash.equalsIgnoreCase(frontendHash)) {
            throw new IllegalArgumentException("Hash mismatch – файл було пошкоджено або змінено");
        }

        boolean exist = this.existsByFileHashAndUser_Id(computedHash, ownerId);

        String realFileName = file.getOriginalFilename();
        String pre_UUID = String.valueOf(UUID.randomUUID());

        realFileName = Normalizer.normalize(Objects.requireNonNull(realFileName), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        realFileName = realFileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        String publicUrl = "";
        if (!exist) {
            publicUrl = supabaseStorageService.uploadFile(realFileName, pre_UUID, bytes, file.getContentType());
        }else{
            publicUrl = fileRepository.findByFileHashAndUser_Id(computedHash, ownerId).get().getPath();
        }

        File entity = new File();
        entity.setFileName(pre_UUID + realFileName);
        entity.setFileRealName(file.getOriginalFilename());
        entity.setFileHash(computedHash);
        entity.setUser(userService.findById(ownerId));
        entity.setFileType(file.getContentType());
        entity.setFileSize(String.valueOf(file.getSize()));
        entity.setPath(publicUrl);

        return fileRepository.save(entity).getPath();
    }


    @Override
    public PaginationListResponse<FileSimpleResponse> findByUserId(Long userId, int page, int size) {
        log.info("Service: Find files by user id {}", userId);
        PaginationListResponse<FileSimpleResponse> response = new PaginationListResponse<>();
        Page<File> byUserId = fileRepository.findByUser_Id(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate")));
        response.setTotalPages(byUserId.getTotalPages());
        response.setContent(byUserId.stream().map(fileMapper::toSimpleResponse).collect(Collectors.toList()));
        return response;
    }


    @Override
    public void delete(long id) {
        log.info("Service: Delete file with id {}", id);
        File file = fileRepository.findById(id).orElseThrow();
        String filename = file.getFileName();
        log.info("Service: Delete file with id {}", filename);
        supabaseStorageService.deleteFile(filename);
        fileRepository.deleteById(id);
    }

    @Override
    public boolean existsByFileHashAndUser_Id(String fileHash, Long userId) {
        log.info("Service: Exists file with hash {} and user id {}", fileHash, userId);
        return fileRepository.findByFileHashAndUser_Id(fileHash, userId).isPresent();
    }

    private String sha256(byte[] bytes) throws NoSuchAlgorithmException {
        log.info("Service: Sha256 hash <UNK> <UNK> <UNK> <UNK> <UNK>");
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
