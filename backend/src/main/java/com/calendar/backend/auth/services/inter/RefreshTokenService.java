package com.calendar.backend.auth.services.inter;

import com.calendar.backend.auth.models.RefreshToken;
import com.calendar.backend.models.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface RefreshTokenService {
    String createRefreshToken(User user, HttpServletRequest request);
    void delete(User user, HttpServletRequest request);
    Optional<RefreshToken> findByUser(User user, HttpServletRequest request);
}
