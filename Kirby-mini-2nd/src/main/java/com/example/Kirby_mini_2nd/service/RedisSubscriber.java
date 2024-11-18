package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final MyWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신된 메시지를 ChatMessage 객체로 역직렬화
            ChatMessage chatMessage = objectMapper.readValue(message.getBody(), ChatMessage.class);
            System.out.println("Redis 메시지 수신: " + chatMessage);

            // WebSocket을 통해 모든 연결된 클라이언트에게 메시지를 전송
            webSocketHandler.broadcastMessage(objectMapper.writeValueAsString(chatMessage));
        } catch (Exception e) {
            System.err.println("메시지 파싱 오류: " + e.getMessage());
        }
    }
}
