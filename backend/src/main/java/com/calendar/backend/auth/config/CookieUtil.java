package com.calendar.backend.auth.config;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

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
}
