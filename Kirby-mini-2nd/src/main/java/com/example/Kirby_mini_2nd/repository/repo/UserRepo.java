package com.example.Kirby_mini_2nd.repository.repo;


import com.example.Kirby_mini_2nd.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepo extends JpaRepository<User, String> {

    // 아이디로 사용자 조회
    Optional<User> findByUserId(String userId);

    // 로그인 인증
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.userPw = :userPw")
    Optional<User> findByUserIdAndUserPw(@Param("userId") String userId, @Param("userPw") String userPw);

    // 아이디 중복 체크
    @Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId")
    int countByUserId(@Param("userId") String userId);
}

