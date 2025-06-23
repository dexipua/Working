package com.calendar.backend.auth.services.inter;

public interface VerificationCodeService {
    String createVerificationCode(String email);
    boolean checkVerificationCode(String email, String token);
}
