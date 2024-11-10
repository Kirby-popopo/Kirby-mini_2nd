package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomid")
    private int roomId; // 시퀀스 번호
    @Column(name = "room_name")
    private String room_name; // 채팅방 이름

    @Transient
    private Set<WebSocketSession> sessions = new HashSet<>();
}
