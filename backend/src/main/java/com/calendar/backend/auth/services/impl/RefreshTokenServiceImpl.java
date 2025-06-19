package com.calendar.backend.auth.services.impl;

import com.calendar.backend.auth.models.RefreshToken;
import com.calendar.backend.auth.repositories.RefreshTokenRepository;
import com.calendar.backend.auth.services.inter.RefreshTokenService;
import com.calendar.backend.models.User;
import com.calendar.backend.services.inter.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${RT_TIME}")
    private Long refreshTokenExpirationMs;

    private final RefreshTokenRepository repository;
    private final UserService userService;

    public String createRefreshToken(User user, HttpServletRequest request) {
        log.info("create refresh token for user: {}", user.getEmail());

        String token = UUID.randomUUID().toString();
        LocalDateTime expirationTime = LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000);

        String userAgent = request.getHeader("User-Agent");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);

        refreshToken.setUser(user);
        refreshToken.setCreatingTime(LocalDateTime.now());
        refreshToken.setExpirationTime(expirationTime);

        refreshToken.setIpAddress(extractClientIp(request));
        refreshToken.setUserAgent(userAgent);
        refreshToken.setDevice(parseDevice(userAgent));

        repository.save(refreshToken);
        return token;
    }

    @Transactional
    public void delete(Long userId, HttpServletRequest request) {
        log.info("delete a refresh tokens for user with id: {}", userId);
        repository.deleteByUser_IdAndIpAddress(userId, extractClientIp(request));
    }


    public Optional<RefreshToken> findByUser(Long userId, HttpServletRequest request) {
        log.info("try to find refresh token by user id: {}", userId);
        return repository.findByUser_IdAndIpAddress(userId, extractClientIp(request));
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private String parseDevice(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "Mac";
        if (userAgent.contains("X11")) return "Unix";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iPhone";
        return "Other";
    }
}