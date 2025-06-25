package com.calendar.backend.auth.controllers;

import com.calendar.backend.models.User;
import com.calendar.backend.services.inter.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.calendar.backend.auth.utils.CookieUtil.createCookie;
import static com.calendar.backend.auth.utils.CookieUtil.deleteCookie;
import static com.calendar.backend.auth.utils.SecurityUtil.getRole;
import static org.springframework.http.CacheControl.maxAge;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final WebClient webClient;

    @Value("${realm}")
    private String realm;
    @Value("${client-secret}")
    private String clientSecret;
    @Value("${client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.coffee-programmers-client.redirect-uri}")
    private String redirectUri;
    @Value("${jwt-time}")
    private Long jwtTime;
    private final UserService userService;
    private final JwtDecoder jwtDecoder;

    @PostMapping("/callback")
    public Mono<ResponseEntity<Void>> exchangeCode(@RequestParam String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        formData.add("client_id", clientId);
        if (clientSecret != null) {
            formData.add("client_secret", clientSecret);
        }

        return webClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/realms/" + realm + "/protocol/openid-connect/token")
                                .build()
                )
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(formData)
                .retrieve().bodyToMono(Map.class)
                .map(response -> {
                    log.info("Auth controller: response from code exchange: " + response);

                    String accessTokenString = (String) response.get("access_token");
                    String idTokenString = (String) response.get("id_token");

                    Jwt idToken = jwtDecoder.decode(idTokenString);
                    Jwt accessToken = jwtDecoder.decode(accessTokenString);

                    String email = idToken.getClaim("email");
                    String role = getRole(accessToken);
                    User user = new User();
                    user.setEmail(email);
                    user.setBirthday(idToken.getClaim("birthday"));
                    user.setFirstName(idToken.getClaim("given_name"));
                    user.setLastName(idToken.getClaim("family_name"));
                    user.setDescription(idToken.getClaim("description"));
                    user.setKeycloakUserId(idToken.getClaim("sub"));
                    user.setRole(role);
                    if (userService.isNotExistByEmail(email)) {
                        user.setId(userService.create(user).getId());
                    } else {
                        userService.updateUserKeycloak(user, userService.findUserByEmail(email).getId());
                    }

                    ResponseCookie cookie = createCookie("accessToken", accessTokenString, jwtTime, false);
                    ResponseCookie userIdCookie = createCookie("userId", String.valueOf(user.getId()), -1, false);
                    ResponseCookie idTokenCookie = createCookie("idToken", idTokenString, jwtTime, false);
                    ResponseCookie roleCookie = createCookie("role", role, -1, false);

                    return ResponseEntity
                            .ok()
                            .headers(httpHeaders -> {
                                httpHeaders.put(HttpHeaders.SET_COOKIE, List.of(
                                        cookie.toString(),
                                        userIdCookie.toString(),
                                        idTokenCookie.toString(),
                                        roleCookie.toString())
                                );
                            })
                            .build();
                });
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("AuthController: Logout user");
        System.out.println("AuthController: Logout user: " + redirectUri);
        return ResponseEntity.ok()
                .headers(headers ->
                        headers.put(HttpHeaders.SET_COOKIE, List.of(
                                deleteCookie("accessToken").toString(),
                                deleteCookie("userId").toString(),
                                deleteCookie("role").toString(),
                                deleteCookie("idToken").toString())))
                .build();
    }
}
