package com.calendar.backend.auth.services.inter;

public interface EmailService {

    void sendCodeEmail(String recipient, String code);

}
