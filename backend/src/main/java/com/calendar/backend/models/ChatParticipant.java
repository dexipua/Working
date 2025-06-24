package com.calendar.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "chat_participants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chat_id", "user_id"})
})
@Getter
@Setter
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime joinedAt = LocalDateTime.now();
}
