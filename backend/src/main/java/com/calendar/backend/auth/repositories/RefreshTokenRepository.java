package com.calendar.backend.auth.repositories;

import com.calendar.backend.auth.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    void deleteByUser_IdAndIpAddress(long userId, String ipAddress);
    List<RefreshToken> findAllByUser_Id(long userId);
    Optional<RefreshToken> findByUser_IdAndIpAddress(long userId, String ipAddress);
}
