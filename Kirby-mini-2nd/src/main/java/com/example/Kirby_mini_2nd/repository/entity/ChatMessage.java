package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type; // 메시지 타입, 필요시 Enum으로 메시지 유형 (JOIN, LEAVE, CHAT 등) 관리

    private String message; // 메시지 본문
    private String sendDate; // 메시지를 보낸 시간

    private int roomId; // 발송지인 채팅방 ID
    private String sender; // 발송자

    // 직렬화 버전을 식별하기 위한 ID
    private static final long serialVersionUID = 1L;

    // 메시지를 전송할 때, 현재 시간을 "yyyy-MM-dd'T'HH:mm:ss.SSSX" 형식의 문자열로 변환하여 설정
    public void setSendDateNow() {
        this.sendDate = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
