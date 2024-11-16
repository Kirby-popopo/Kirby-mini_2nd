package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.User;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthSvc {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    @Autowired
    public AuthSvc(PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }
    
    // 회원가입 패스워드 암호화

    public User signUp(String userId, String userPw) {
        String endcodePw = passwordEncoder.encode(userPw);
        User user = User.builder()
                .userId(userId)
                .userPw(endcodePw)
                .build();

       /* validateDuplicateMember(user);*/

        return userRepo.save(user);
    }
    
    // 아이디 중복 확인
    private void validateDuplicateMember(User user) {
        userRepo.findById(user.getUserIdx())
                .ifPresent(u -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }
}
