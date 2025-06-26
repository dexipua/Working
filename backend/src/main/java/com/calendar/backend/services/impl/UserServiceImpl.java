package com.calendar.backend.services.impl;

import com.calendar.backend.dto.wrapper.PaginationListResponse;
import com.calendar.backend.dto.wrapper.PasswordRequest;
import com.calendar.backend.models.User;
import com.calendar.backend.repositories.UserRepository;
import com.calendar.backend.repositories.specification.UserSpecification;
import com.calendar.backend.services.inter.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RealmResource realmResource;
    private final ClientResource clientResource;
    private final String clientUUID;
    private final Map<String, RoleRepresentation> clientRoles;

    private final WebClient webClient;

    @Value("${realm}")
    private String realm;
    @Value("${client-id}")
    private String clientId;
    @Value("${client-secret}")
    private String clientSecret;

    @Override
    public User createUserKeycloak(User user) {
        log.info("Service: Saving new user {}", user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User createUser(User user, String password) {
        log.info("Service: Saving new user {}", user.getEmail());
        String email = user.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EntityExistsException("User with email " + email + " already exists");
        }

        UserRepresentation userRepresentation = new UserRepresentation();

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.singleAttribute("birthday", String.valueOf(user.getBirthday()));
        userRepresentation.singleAttribute("description", user.getDescription());
        userRepresentation.setEnabled(true);

        Response response = realmResource.users().create(userRepresentation);
        if (response.getStatus() == 201) {
            URI location = response.getLocation();
            String path = location.getPath();
            String userId = path.substring(path.lastIndexOf('/') + 1);
            user.setKeycloakUserId(userId);
        } else {
            log.info("Service: Failed to create user. Status: " + response.getStatus());
            String error = response.readEntity(String.class);
            log.info("Service: Error response: " + error);
        }
        response.close();

        realmResource.users()
                .get(user.getKeycloakUserId())
                .roles()
                .clientLevel(clientUUID)
                .add(List.of(clientRoles.get(user.getRole())));

        return createUserKeycloak(user);
    }

    @Override
    public User updateUser(User newUser, long userId) {
        log.info("Service: Updating user with id {}", userId);

        User userToUpdate = findById(userId);
        checkForDeletedUser(userToUpdate);

        String newFirstName = newUser.getFirstName();
        String newLastName = newUser.getLastName();
        LocalDate newBirthday = newUser.getBirthday();
        String newDescription = newUser.getDescription();

        String keycloakUserId = userToUpdate.getKeycloakUserId();

        userToUpdate.setFirstName(newFirstName);
        userToUpdate.setLastName(newLastName);
        userToUpdate.setDescription(newDescription);
        userToUpdate.setBirthday(newBirthday);

        UserRepresentation userRepresentation = realmResource.users().get(keycloakUserId).toRepresentation();
        userRepresentation.setFirstName(newFirstName);
        userRepresentation.setLastName(newLastName);
        userRepresentation.singleAttribute("description", newDescription);
        userRepresentation.singleAttribute("birthday", String.valueOf(newBirthday));

        realmResource.users().get(keycloakUserId).update(userRepresentation);

        return userRepository.save(userToUpdate);
    }

    @Override
    public User updateUserKeycloak(User newUser, long userId) {
        log.info("Service: Updating user with id {}", userId);

        User userToUpdate = findById(userId);
        checkForDeletedUser(userToUpdate);

        userToUpdate.setFirstName(newUser.getFirstName());
        userToUpdate.setLastName(newUser.getLastName());
        userToUpdate.setDescription(newUser.getDescription());
        userToUpdate.setBirthday(newUser.getBirthday());
        userToUpdate.setEmail(newUser.getEmail());
        userToUpdate.setRole(newUser.getRole());

        return userRepository.save(userToUpdate);
    }

    private boolean verifyOldPassword(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);

        try {
            webClient.post()
                    .uri("/realms/" + realm + "/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return true;
        } catch (WebClientResponseException e) {
            return false;
        }
    }

    @Override
    public boolean updatePassword(PasswordRequest passwordRequest, Authentication authentication) {
        String keycloakUserId = authentication.getName();
        String email = findUserByKeycloakUserId(keycloakUserId).getEmail();

        log.info("Service: Updating password for user with email {}", email);

        boolean isOldPasswordValid = verifyOldPassword(email, passwordRequest.getOldPassword());
        if (!isOldPasswordValid) {
            log.warn("Service: Old password is incorrect for user {}", email);
            throw new IllegalArgumentException("Old password is incorrect");
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(passwordRequest.getNewPassword());
        credential.setTemporary(false);

        realmResource.users().get(keycloakUserId).resetPassword(credential);

        log.info("Password successfully updated for user {}", email);
        return true;
    }

    @Override
    public void delete(long id) {
        log.info("Service: Deleting user with id {}", id);
        User user = findById(id);
        checkForDeletedUser(user);

        realmResource.users().delete(user.getKeycloakUserId());
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
        return findUserByKeycloakUserId(authentication.getName());
    }

    @Override
    public User findUserByKeycloakUserId(String keycloakUserId) {
        log.info("Service: Finding user by keycloakUserId {}", keycloakUserId);
        return userRepository.findByKeycloakUserId(keycloakUserId).orElseThrow(
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
            filters.put("role", role.toUpperCase());
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
