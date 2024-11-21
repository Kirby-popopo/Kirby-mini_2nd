package com.example.Kirby_mini_2nd.repository.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pw")
    private String userPw;

    private String email;
    private String name;
    private String gender;
    private String nickname;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "profile_image")
    private String profileImage;

    private String description; // ???

    // OAuth2 가입을 위한 생성자
    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

}