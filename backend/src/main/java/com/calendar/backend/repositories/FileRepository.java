package com.calendar.backend.repositories;

import com.calendar.backend.models.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File,Long> {
    Page<File> findByUser_Id(Long userId, Pageable pageable);
    Optional<File> findByFileHashAndUser_Id(String fileHash, Long userId);
}
