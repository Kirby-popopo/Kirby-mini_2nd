package com.example.Kirby_mini_2nd.repository.repo;


import com.example.Kirby_mini_2nd.model.dto.ProfileDTO;
import com.example.Kirby_mini_2nd.model.dto.SearchUserDTO;
import com.example.Kirby_mini_2nd.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    // 아이디로 사용자 조회
    Optional<User> findByUserId(String userId);

    // 로그인 인증
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.userPw = :userPw")
    Optional<User> findByUserIdAndUserPw(@Param("userId") String userId, @Param("userPw") String userPw);

    // 아이디 중복 체크
    @Query("SELECT COUNT(u) FROM User u WHERE u.userId = :userId")
    int countByUserId(@Param("userId") String userId);

    // 검색용 아이디 찾기
    @Query("SELECT new com.example.Kirby_mini_2nd.model.dto.SearchUserDTO(u.userId, u.profileImage, u.name) FROM User u WHERE u.userId LIKE :id%")
    List<SearchUserDTO> findByUserIdLikeTargetId(@Param("id") String id);

    // 프로필 검색
    @Query("SELECT new com.example.Kirby_mini_2nd.model.dto.ProfileDTO(u.name, u.profileImage, u.description) FROM User u WHERE u.userId = :id")
    ProfileDTO findByUserIdForProfile(@Param("id") String id);

}

