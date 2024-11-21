package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@ToString
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomid")
    private int roomId; // 시퀀스 번호

    @ElementCollection
    @CollectionTable(name = "chat_room_participants", joinColumns = @JoinColumn(name = "roomid"))
    @Column(name = "participant_id")
    private Set<String> participants = new HashSet<>();

    @Column(name = "room_name", nullable = false)
    private String roomName; // 카멜 케이스로 네이밍 규칙 일관성 유지

    @Transient
    private Set<WebSocketSession> sessions = new HashSet<>();

    public ChatRoom(String roomName, Set<String> participants) {
        this.roomName = roomName;
        this.participants = participants != null ? new HashSet<>(participants) : new HashSet<>();
    }
}
