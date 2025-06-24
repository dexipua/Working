package com.calendar.backend.services.inter;

import com.calendar.backend.dto.user.UserFullResponse;
import com.calendar.backend.dto.user.UserListResponse;
import com.calendar.backend.dto.user.UserUpdateRequest;
import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.dto.wrapper.PasswordRequest;
import com.calendar.backend.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User create(User user);
    User update(User user, long userId);
    void delete(long id);
    User findById(long id);
    PaginationListResponse<User> findAll(
            String email, String firstName, String lastName, String role,
            int page, int size, Authentication auth);
    User findUserByAuth(Authentication authentication);
    User findUserByEmail(String email);
    boolean isNotExistByEmail(String email);
}
