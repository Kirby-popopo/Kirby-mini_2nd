package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@IdClass(FollowsCompositekey.class)
@Getter
@Setter
@ToString
public class Follows {
    @Id
    @Column(name = "follower_id", nullable = false, length = 30)
    private String follower_id;

    @Id
    @Column(name = "following_id", nullable = false, length = 30)
    private String following_id;

    private LocalDateTime follow_time;
}
