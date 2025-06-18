package com.calendar.backend.auth.models;

import com.calendar.backend.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;
    @ManyToOne
    private User user;
    private LocalDateTime creatingTime;
    private LocalDateTime expirationTime;
    private String ipAddress;
    private String userAgent;
    private String device;
}
