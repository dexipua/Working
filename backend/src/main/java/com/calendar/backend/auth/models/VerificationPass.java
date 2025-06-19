package com.calendar.backend.auth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_passes")
@Getter
@Setter
@NoArgsConstructor
public class VerificationPass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;
    private LocalDateTime creatingTime;
    private LocalDateTime expirationTime;
}
