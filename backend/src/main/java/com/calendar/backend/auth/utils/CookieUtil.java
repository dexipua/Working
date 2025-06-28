package com.calendar.backend.auth.utils;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class CookieUtil {
    public static String getCookie(Cookie[] cookies, String name) {
        if(cookies != null) {
            log.info("COOKIES: {}", Arrays.stream(cookies).map(cookie -> cookie.getName() + ":" + cookie.getValue()).collect(Collectors.joining(", ")));
        } else {
            log.info("COOKIES: NULL!");
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static ResponseCookie deleteCookie(String name) {
        return ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .build();
    }

    public static ResponseCookie createCookie(String name, String value, long maxAge, boolean httpOnly) {
        ResponseCookie.ResponseCookieBuilder responseCookieBuilder = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .path("/");

        if (maxAge != -1) {
            responseCookieBuilder.maxAge(Duration.ofSeconds(maxAge));
        }

        return responseCookieBuilder.build();
    }
}
