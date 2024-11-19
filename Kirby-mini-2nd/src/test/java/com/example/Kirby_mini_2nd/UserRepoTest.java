//package com.example.Kirby_mini_2nd;
//
//
//import com.example.Kirby_mini_2nd.repository.entity.User;
//import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DataJpaTest
//public class UserRepoTest {
//
//    @Autowired
//    private UserRepo repository;
//
//    @BeforeEach
//    void pr() {
//        System.out.println("=".repeat(80));
//    }
//    //가입
//    @Test
//    @Rollback(value = false)
//    void insertUser(){
//        User user = new User();
//        user.setUserId("1234");
//        user.setUserPw("1234");
//        user.setEmail("1234@google.com");
//        user.setName("1234");
//        user.setGender("F");
//        user.setNickname("Sleep");
//        user.setPhoneNumber("012-3456-7890");
//        user.setLastLogin(LocalDateTime.now());
//
//        User save = repository.save(user);
//    }
//
//    // 중복 아이디 확인
//    @Test
//    void findByUserId(){
//        Optional<User> foundUser = repository.findByUserId("1234");
//        if(foundUser.isEmpty()){
//            System.out.println("존재하지 않음");
//        } else {
//            System.out.println(foundUser.get());
//        }
//    }
//
//    // 삭제
//    @Test
//    void delete(){
//        repository.deleteById("1");
//    }
//
//    // 업데이트
//    @Test
//    @Transactional
//    @Rollback(value = false)
//    void update() {
//        User user = repository.findByUserId("1234").get();
//        user.setNickname("2345");
//        user.setName("2345");
//    }
//}
