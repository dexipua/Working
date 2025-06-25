//package com.calendar.backend.auth.config;
//
//import com.calendar.backend.models.User;
//import com.calendar.backend.services.inter.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component("userSecurity")
//@RequiredArgsConstructor
//public class UserSecurity {
//
//    private final UserService userService;
//
//    private boolean checkUser(Authentication authentication, String username) {
//        log.info("preAuth: Checking user {}", username);
//        UserDetails user = (UserDetails) authentication.getPrincipal();
//        String authenticatedUserEmail = user.getUsername();
//        return authenticatedUserEmail.equals(username);
//    }
//}
