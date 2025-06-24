package com.calendar.backend.services.impl;

import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.dto.wrapper.PasswordRequest;
import com.calendar.backend.models.User;
import com.calendar.backend.models.enums.Role;
import com.calendar.backend.repositories.UserRepository;
import com.calendar.backend.repositories.specification.UserSpecification;
import com.calendar.backend.services.inter.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public User create(User userCreateRequest) {
        log.info("Service: Saving new user {}", userCreateRequest);

        if (userRepository.findByEmail(userCreateRequest.getEmail()).isPresent()) {
            throw new EntityExistsException("User with email " +
                    userCreateRequest.getEmail() + " already exists");
        }

        userCreateRequest.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        return userRepository.save(userCreateRequest);
    }

    @Override
    public User update(User userUpdateData, long userId) {
        log.info("Service: Updating user with id {}", userId);

        User userToUpdate = findById(userId);
        checkForDeletedUser(userToUpdate);

        userToUpdate.setFirstName(userUpdateData.getFirstName());
        userToUpdate.setLastName(userUpdateData.getLastName());
        userToUpdate.setDescription(userUpdateData.getDescription());
        userToUpdate.setBirthday(userUpdateData.getBirthday());

        return userRepository.save(userToUpdate);
    }

    @Override
    public void delete(long id) {
        log.info("Service: Deleting user with id {}", id);
        checkForDeletedUser(findById(id));
        userRepository.deleteById(id);
    }

    @Override
    public User findById(long id) {
        log.info("Service: Finding user with id {}", id);
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public PaginationListResponse<User> findAll(
            String email, String firstName, String lastName, String role, int page, int size, Authentication auth) {

        Map<String, Object> filters = createFilters(email, firstName, lastName, role);
        User user = findUserByAuth(auth);

        log.info("Service: Finding all users with filters {}", filters);

        Page<User> users = userRepository.findAll(
                UserSpecification.filterUsers(filters)
                        .and(UserSpecification.notUser(user.getId()))
                        .and(UserSpecification.notIncludeDeleted()),
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "lastName", "firstName")));
        PaginationListResponse<User> paginationListResponse = new PaginationListResponse<>();
        paginationListResponse.setTotalPages(users.getTotalPages());
        paginationListResponse.setContent(users.getContent());
        return paginationListResponse;
    }

    @Override
    public User findUserByAuth(Authentication authentication) {
        log.info("Service: Finding user by authentication {}", authentication);
        return userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }

    @Override
    public boolean isNotExistByEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info("Service: Finding user by email {}", email);
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found with email " + email));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Service: Finding user details with email by loading {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Map<String, Object> createFilters(String email, String firstName, String lastName, String role) {
        log.info("Service: Creating filters for user with email {}, first name {}, last name {} and role {}",
                email, firstName, lastName, role);

        Map<String, Object> filters = new HashMap<>();
        if (email != null && !email.isBlank() && !email.equals("null")) {
            filters.put("email", email);
        }
        if (firstName != null && !firstName.isBlank() && !firstName.equals("null")) {
            filters.put("firstName", firstName);
        }
        if (lastName != null && !lastName.isBlank() && !lastName.equals("null")) {
            filters.put("lastName", lastName);
        }
        if (role != null && !role.isBlank() && !role.equals("null")) {
            filters.put("role", Role.valueOf(role.toUpperCase()).getLevel());
        }

        return filters;
    }

    private void checkForDeletedUser(User user) {
        log.info("Service: Checking for deleted user with id {}", user.getId());

        if ("!deleted-user!@deleted.com".equals(user.getEmail())) {
            log.info("Service: User with id {} is deleted", user.getId());
            throw new EntityExistsException("Can`t do anything with deleted user");
        }
    }
}
