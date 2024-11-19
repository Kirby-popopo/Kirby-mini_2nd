package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query("SELECT c FROM ChatRoom c WHERE c.roomId NOT IN " +
            "(SELECT u.roomId FROM UserChatExit u WHERE u.userId = :userId)")
    List<ChatRoom> findAvailableRooms(String userId);

    List<ChatRoom> findAllByRoomIdNotIn(List<Integer> exitedRoomIds);
}
