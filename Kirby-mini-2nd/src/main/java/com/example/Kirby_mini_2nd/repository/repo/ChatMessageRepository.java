package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomId(int roomId, Sort sendDate);

    void deleteAllByRoomId(int roomId);

    // 채팅방 ID로 메시지를 페이징 처리하여 가져오기
    Page<ChatMessage> findByRoomId(Long roomId, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.roomId = :roomId AND m.sendDate < :beforeDateTime ORDER BY m.sendDate DESC")
    List<ChatMessage> findPreviousMessages(@Param("roomId") int roomId,
                                           @Param("beforeDateTime") LocalDateTime beforeDateTime,
                                           Pageable pageable);

    @Query("SELECT m FROM ChatMessage m WHERE m.roomId = :roomId AND m.sendDate < :before ORDER BY m.sendDate DESC")
    List<ChatMessage> findMessagesBefore(
            @Param("roomId") int roomId,
            @Param("before") LocalDateTime before);
}
