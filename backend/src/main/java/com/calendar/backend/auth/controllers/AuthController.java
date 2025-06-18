package com.calendar.backend.auth.controllers;

import com.calendar.backend.auth.config.JwtUtils;
import com.calendar.backend.auth.dto.AuthResponse;
import com.calendar.backend.auth.dto.LogInRequest;
import com.calendar.backend.auth.services.impl.RefreshTokenServiceImpl;
import com.calendar.backend.dto.wrapper.StringRequest;
import com.calendar.backend.models.User;
import com.calendar.backend.services.inter.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserService userService;
    private final JwtUtils jwtUtils;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/checks")
    public boolean checkEmail(@RequestBody StringRequest requestEmail) {
        userService.loadUserByUsername(requestEmail.getText());
        return true;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LogInRequest loginRequest,
                      HttpServletRequest request, HttpServletResponse response) {
        log.info("Login user {}", loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        User user = (User) authentication.getPrincipal();

        String username =user.getUsername();

        refreshTokenService.delete(user, request);
        String token = refreshTokenService.createRefreshToken(user, request);

        Map<String, Object> claims = new HashMap<>();
        claims.put("token", token);

        String jwtToken = jwtUtils.generateTokenFromUsername(username, claims);

        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", jwtToken).httpOnly(true).path("/").maxAge(Duration.ofDays(3)).build();
        ResponseCookie userIdCookie = ResponseCookie.from("userId", String.valueOf(user.getId())).path("/").build();
        ResponseCookie roleCookie = ResponseCookie.from("role", String.valueOf(user.getRole())).path("/").build();


        return ResponseEntity.ok()
                .headers(headers -> {
                    headers.add(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                    headers.add(HttpHeaders.SET_COOKIE, userIdCookie.toString());
                    headers.add(HttpHeaders.SET_COOKIE, roleCookie.toString());
                })
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public void logout(Authentication authentication, HttpServletRequest request) {
        log.info("Logout user {}", authentication);
        refreshTokenService.delete(userService.findUserByAuth(authentication), request);
    }
}
