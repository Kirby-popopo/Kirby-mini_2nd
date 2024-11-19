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

    public User signUp(User user) {
        String endcodePw = passwordEncoder.encode(user.getUserPw());
        User signUpUser = User.builder()
                .userId(user.getUserId())
                .userPw(endcodePw)
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .lastLogin(user.getLastLogin())
                .profileImage("http://localhost:8090/images/default.jpg")
                .description(user.getDescription())
                .build();

        return userRepo.save(signUpUser);
    }


    public int countByUserId(String userId) {
        return userRepo.countByUserId(userId);
    }

    // 아이디 중복 확인
    private void validateDuplicateUser(String userId) {
        int count = userRepo.countByUserId(userId);
        if(count > 0) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
            }
    }
}
