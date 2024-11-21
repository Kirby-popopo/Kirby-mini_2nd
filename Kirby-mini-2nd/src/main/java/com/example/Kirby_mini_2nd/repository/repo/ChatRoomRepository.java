package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    // 사용자가 나가지 않은 채팅방 조회
    @Query("SELECT c FROM ChatRoom c " +
            "LEFT JOIN UserChatExit u ON c.roomId = u.roomId " +
            "WHERE u.userId <> :userId OR u.userId IS NULL")
    List<ChatRoom> findAvailableRooms(String userId);

    List<ChatRoom> findAllByRoomIdNotIn(List<Integer> exitedRoomIds);

    // 참여자 기준 채팅방 찾기
    @Query("SELECT c FROM ChatRoom c WHERE SIZE(c.participants) = :size AND :userId IN (SELECT p FROM c.participants p)")
    Optional<ChatRoom> findByParticipantsExact(@Param("userId") String userId, @Param("size") int size);


    // 특정 사용자가 포함된 모든 채팅방 조회
    @Query("SELECT c FROM ChatRoom c WHERE :userId MEMBER OF c.participants")
    List<ChatRoom> findRoomsByUserId(String userId);

    // 특정 사용자가 포함된 채팅방 존재 여부 확인
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM ChatRoom c WHERE :userId MEMBER OF c.participants")
    boolean existsByUserId(String userId);

    @Query(value = "SELECT * " +
            "FROM chat_room c " +
            "WHERE (SELECT COUNT(*) " +
            "       FROM chat_room_participants p " +
            "       WHERE p.room_id = c.roomid) = :size " +
            "  AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM chat_room_participants p " +
            "    WHERE p.room_id = c.roomid " +
            "      AND p.participant_id NOT IN (:participants))",
            nativeQuery = true)
    Optional<ChatRoom> findByExactParticipants(@Param("participants") List<String> participants,
                                               @Param("size") int size);
}
