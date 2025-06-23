package com.calendar.backend.auth.services.impl;

import com.calendar.backend.auth.models.VerificationCode;
import com.calendar.backend.auth.repositories.VerificationCodeRepository;
import com.calendar.backend.auth.services.inter.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationCodeRepository verificationPassRepository;

    @Override
    public String createVerificationCode(String email) {
        return verificationPassRepository.save(new VerificationCode(email)).getToken();
    }

    @Override
    public boolean checkVerificationCode(String email, String token) {
        return verificationPassRepository.existsByEmailAndExpirationTimeAfterAndTokenEquals(email, LocalDateTime.now(), token);
    }
}
