package com.calendar.backend.repositories;

import com.calendar.backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);

    Optional<Message> findTopByChatIdOrderBySentAtDesc(Long chatId); // останнє повідомлення
}
