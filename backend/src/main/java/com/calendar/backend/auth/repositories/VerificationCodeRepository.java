package com.calendar.backend.auth.repositories;

import com.calendar.backend.auth.models.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    boolean existsByEmailAndExpirationTimeAfterAndTokenEquals(String email, LocalDateTime expirationTimeAfter, String token);

}
