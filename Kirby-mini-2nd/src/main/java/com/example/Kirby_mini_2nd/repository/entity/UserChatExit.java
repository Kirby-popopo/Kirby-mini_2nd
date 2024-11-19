package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user_chat_exit")
public class UserChatExit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    public UserChatExit(String userId, Integer roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }
}
