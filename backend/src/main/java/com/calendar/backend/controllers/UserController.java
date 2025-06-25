package com.calendar.backend.controllers;

import com.calendar.backend.dto.user.UserCreateRequest;
import com.calendar.backend.dto.user.UserFullResponse;
import com.calendar.backend.dto.user.UserListResponse;
import com.calendar.backend.dto.user.UserUpdateRequest;
import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.mappers.UserMapper;
import com.calendar.backend.models.User;
import com.calendar.backend.services.inter.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("Controller: Create user with body: {}", request);
        User user = userMapper.fromUserRequestToUser(request);
        return userMapper.fromUserToUserResponse(userService.create(user));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserFullResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("Controller: Update user with id: {} with body: {}", id, request);
        User user = userMapper.fromUserRequestToUser(request);
        return userMapper.fromUserToUserResponse(userService.updateUser(user, id));
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public UserFullResponse updateMyUser(
            @RequestBody UserUpdateRequest request,
            Authentication auth) {
        log.info("Controller: Update my user with body: {}", request);
        long userId = userService.findUserByAuth(auth).getId();
        User user = userMapper.fromUserRequestToUser(request);
        return userMapper.fromUserToUserResponse(userService.updateUser(user, userId));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Controller: Delete user with id: {}", id);
        userService.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserFullResponse getUser(@PathVariable Long id) {
        log.info("Controller: Get user with id: {}", id);
        return userMapper.fromUserToUserResponse(userService.findById(id));
    }

    @GetMapping("/my")
    @ResponseStatus(HttpStatus.OK)
    public UserFullResponse getMyUser(Authentication auth) {
        log.info("Controller: Get my user");
        return userMapper.fromUserToUserResponse(userService.findUserByAuth(auth));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PaginationListResponse<UserListResponse> getAllUsers(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String role,
            Authentication auth
    ) {
        log.info("Controller: Get all users");
        PaginationListResponse<User> users = userService.findAll(email, firstName, lastName, role, page, size, auth);
        PaginationListResponse<UserListResponse> responses = new PaginationListResponse<>();
        responses.setContent(users.getContent().stream().map(userMapper::fromUserToUserListResponse).toList());
        responses.setTotalPages(users.getTotalPages());
        return responses;
    }
}