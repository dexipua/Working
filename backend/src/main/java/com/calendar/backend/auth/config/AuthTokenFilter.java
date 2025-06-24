package com.calendar.backend.auth.config;

import com.calendar.backend.auth.models.RefreshToken;
import com.calendar.backend.auth.services.impl.RefreshTokenServiceImpl;
import com.calendar.backend.models.User;
import com.calendar.backend.services.impl.UserServiceImpl;
import com.calendar.backend.services.inter.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserService userDetailsService;
    private final JwtUtils jwtUtils;

    public AuthTokenFilter(JwtUtils jwtUtils, UserService userDetailsService,
                           RefreshTokenServiceImpl refreshTokenService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Auth: Path - {}", request.getServletPath());
        try {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            if (request.getServletPath().equals("/api/auth/login")
                    || request.getServletPath().equals("/api/auth/regis")
                    || request.getServletPath().equals("/api/auth/logout")) {
                log.info("Auth: Exception path!");
                filterChain.doFilter(request, response);
                return;
            }

            log.info("Auth: Processing JWT token");
            String token = getAccessToken(request);
            log.info("AUTH: Token - {}", token);

            if (!jwtUtils.validateToken(token, request)) {
                log.error("Auth: Invalid JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                setHeaders(response);
                return;
            }

            log.info("Auth: Valid JWT token");
            String username = jwtUtils.getSubject(token);
            User user = userDetailsService.findUserByEmail(username);

            if (jwtUtils.isTokenExpired(token)) {
                log.warn("Auth: Access token expired, refreshing...");

                Optional<RefreshToken> refreshToken = refreshTokenService.findByUser(user.getId(), request);

                if (refreshToken.isEmpty() || jwtUtils.isRefreshTokenExpired(refreshToken.get())) {
                    log.error("Auth: Refresh token is expired or missing");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    setHeaders(response);
                    return;
                }

                log.info("Auth: Refresh token is valid, refreshing access token");

                String newAccessToken = jwtUtils.refreshAccessToken(username, refreshToken.get().getToken());
                response.setStatus(498);
                setHeaders(response);

                response.setHeader("Set-Cookie", "accessToken=" + newAccessToken + ";Path=/;");
                response.setCharacterEncoding("UTF-8");

                return;
            } else {
                log.info("Auth: Access token is valid and does`t expire");
                setAuthenticationContext(token, request);
            }
        } catch (Exception e) {
            log.error("Auth: An error occurred during JWT token processing", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setHeaders(response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void setHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
    }

    private String getAccessToken(HttpServletRequest request) {
        return CookieUtil.getCookie(request.getCookies(), "jwtToken");
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private UserDetails getUserDetails(String token) {
        String jwtSubject = jwtUtils.getSubject(token);
        return userDetailsService.loadUserByUsername(jwtSubject);
    }
}