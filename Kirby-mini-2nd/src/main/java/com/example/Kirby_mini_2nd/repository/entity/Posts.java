package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString

public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int post_pk;
    private String user_id;
    private String contents;
    @CreationTimestamp
    private LocalDateTime post_time;
    private String location;
    private int likes_count;
    private String image_url;
    private String media_url;
}
