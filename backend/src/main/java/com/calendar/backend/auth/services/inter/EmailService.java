package com.calendar.backend.auth.services.inter;

public interface EmailService {

    void sendClaimApprovedEmail(String recipient);

    void sendClaimRejectedEmail(String recipient);

    void sendClaimRequestEmail(String recipient);

    void sendSuccessfullyCreatedAccountEmail(String recipient);

    void sendNewClaimEmail(String recipient);

}
