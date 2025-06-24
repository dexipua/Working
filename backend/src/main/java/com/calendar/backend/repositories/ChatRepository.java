package com.calendar.backend.repositories;

import com.calendar.backend.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Пошук приватного чату між двома юзерами
    @Query("""
        SELECT c FROM Chat c
        JOIN ChatParticipant cp1 ON cp1.chat = c AND cp1.user.id = :userId1
        JOIN ChatParticipant cp2 ON cp2.chat = c AND cp2.user.id = :userId2
        WHERE c.isGroup = false
        GROUP BY c
        HAVING COUNT(cp1) > 0 AND COUNT(cp2) > 0
    """)
    Optional<Chat> findPrivateChatBetweenUsers(@Param("userId1") Long userId1,
                                               @Param("userId2") Long userId2);

    @Query("""
    SELECT c FROM Chat c
    JOIN ChatParticipant cp ON cp.chat = c
    WHERE cp.user.id = :userId
""")
    List<Chat> findAllByParticipantUserId(@Param("userId") Long userId);
}
