package com.calendar.backend.auth.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SecurityUtil {
    private static String clientId;

    @Value("${client-id}")
    private String injectedClientId;

    @PostConstruct
    public void init() {
        clientId = injectedClientId;
    }

    public static String getRole(Jwt accessTokenJwt) {
        return Optional.ofNullable(accessTokenJwt.getClaim("resource_access"))
                .map(resourceAccess -> ((Map<String, Object>) resourceAccess).get(clientId))
                .map(clientAccess -> (Map<String, Object>) clientAccess)
                .map(client -> (List<String>) client.get("roles"))
                .filter(roles -> !roles.isEmpty())
                .map(roles -> roles.get(0))
                .orElse("");
    }
}
