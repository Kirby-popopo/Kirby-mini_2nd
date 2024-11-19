package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomId(int roomId, Sort sendDate);

    void deleteAllByRoomId(int roomId);

    // 채팅방 ID로 메시지를 페이징 처리하여 가져오기
    Page<ChatMessage> findByRoomId(Long roomId, Pageable pageable);
}
