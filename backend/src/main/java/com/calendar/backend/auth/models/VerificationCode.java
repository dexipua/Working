package com.calendar.backend.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_passes")
@Getter
@Setter
@NoArgsConstructor
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String token;
    private LocalDateTime creatingTime;
    private LocalDateTime expirationTime;

    public VerificationCode(String email) {
        this.email = email;
        this.creatingTime = LocalDateTime.now();
        this.expirationTime = LocalDateTime.now().plusMinutes(60);
        this.token = UUID.randomUUID().toString().substring(0, 4);
    }
}
