package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.UserChatExit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserChatExitRepository extends JpaRepository<UserChatExit, Long> {

    // 사용자 ID를 기준으로 나간 방 ID 조회
    @Query("SELECT u.roomId FROM UserChatExit u WHERE u.userId = :userId")
    List<Integer> findExitedRoomIdsByUserId(String userId);

    // 특정 방과 사용자 조합이 존재하는지 확인
    boolean existsByRoomIdAndUserId(int roomId, String userId);
}
