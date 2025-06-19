package com.calendar.backend.auth.services.inter;

import com.calendar.backend.auth.models.RefreshToken;
import com.calendar.backend.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface RefreshTokenService {
    String createRefreshToken(User user, HttpServletRequest request);
    void delete(Long userId, HttpServletRequest request);
    Optional<RefreshToken> findByUser(Long userId, HttpServletRequest request);
}
