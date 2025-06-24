package com.calendar.backend.repositories;

import com.calendar.backend.models.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    List<ChatParticipant> findByUserId(Long userId);

    List<ChatParticipant> findByChatId(Long chatId);
}
