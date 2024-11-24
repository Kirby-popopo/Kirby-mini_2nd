package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_pk;
    private String user_id; //user
    private int post_pk; //post
    private String content;
    @CreationTimestamp
    private LocalDateTime create_at;

    // 기본 생성자 (필수)
    public Comments() {}
}
