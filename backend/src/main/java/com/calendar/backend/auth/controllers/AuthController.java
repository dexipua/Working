package com.calendar.backend.auth.controllers;

import com.calendar.backend.auth.config.CookieUtil;
import com.calendar.backend.auth.config.JwtUtils;
import com.calendar.backend.auth.dto.LogInRequest;
import com.calendar.backend.auth.services.impl.RefreshTokenServiceImpl;
import com.calendar.backend.dto.user.UserCreateRequest;
import com.calendar.backend.dto.user.UserFullResponse;
import com.calendar.backend.dto.wrapper.StringRequest;
import com.calendar.backend.models.User;
import com.calendar.backend.services.inter.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.*;

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
    public boolean checkEmailIsFree(@RequestBody StringRequest requestEmail) {
        log.info("AuthController: check if user with such email {} not exist", requestEmail.getText());
        return userService.isNotExistByEmail(requestEmail.getText());
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/regis")
    public ResponseEntity<Void> regis(@RequestBody @Valid UserCreateRequest regisRequest,
                                      HttpServletRequest request) {
        log.info("AuthController: Regis user {}", regisRequest);

        User user = userService.findByIdForServices(userService.create(regisRequest).getId());

        return creatingTokensAndCookies(user, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LogInRequest loginRequest,
                                      HttpServletRequest request) {
        log.info("AuthController: Login user {}", loginRequest);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        User user = (User) authentication.getPrincipal();

        return creatingTokensAndCookies(user, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        log.info("AuthController: Logout user");
        refreshTokenService.delete(
                Long.parseLong(Objects.requireNonNull(CookieUtil.getCookie(request.getCookies(), "userId"))), request);


        return ResponseEntity.ok()
                .headers(headers ->
                        headers.put(HttpHeaders.SET_COOKIE, List.of(
                                deleteCookie("jwtToken").toString(),
                                deleteCookie("userId").toString(),
                                deleteCookie("role").toString())))
                .build();
    }

    private ResponseEntity<Void> creatingTokensAndCookies(User user, HttpServletRequest request) {
        log.info("AuthController: creating tokens and cookies for user: {}", user);

        String username = user.getUsername();

        refreshTokenService.delete(user.getId(), request);
        String token = refreshTokenService.createRefreshToken(user, request);

        Map<String, Object> claims = new HashMap<>();
        claims.put("token", token);

        String jwtToken = jwtUtils.generateTokenFromUsername(username, claims);

        return createCookies(user, jwtToken);
    }

    private ResponseEntity<Void> createCookies(User user, String token) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", token).httpOnly(true).path("/").maxAge(Duration.ofDays(3)).build();
        ResponseCookie userIdCookie = ResponseCookie.from("userId", String.valueOf(user.getId())).path("/").build();
        ResponseCookie roleCookie = ResponseCookie.from("role", String.valueOf(user.getRole())).path("/").build();

        return ResponseEntity.ok()
                .headers(headers ->
                        headers.put(HttpHeaders.SET_COOKIE, List.of(
                                jwtCookie.toString(),
                                userIdCookie.toString(),
                                roleCookie.toString()
                        )))
                .build();
    }

    private ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }
}
