package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.config.GsonConfig;
import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson = GsonConfig.getGson();

    @Autowired
    public RedisPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishAndSaveMessage(ChannelTopic topic, ChatMessage message) {
        try {
            // 메시지를 JSON 문자열로 변환
            String messageJson = gson.toJson(message);
            String key = "chatRoom:" + message.getRoomId();

            // Redis에 메시지 저장
            redisTemplate.opsForList().rightPush(key, messageJson);
            System.out.println("메시지 Redis에 저장 완료: " + messageJson);

            // Pub/Sub 채널로 메시지 발행
            redisTemplate.convertAndSend(topic.getTopic(), messageJson);
        } catch (Exception e) {
            System.err.println("메시지 직렬화 오류: " + e.getMessage());
        }
    }

    // 채팅방 나가기 메시지.
    public void publishMessage(ChannelTopic topic, ChatMessage leaveMessage) {
        // ChatMessage 객체를 JSON 문자열로 변환
        String messageJson = gson.toJson(leaveMessage);

        // Redis 채널에 메시지 발행
        redisTemplate.convertAndSend(topic.getTopic(), messageJson);

        // 디버깅을 위한 로그 출력
        System.out.println("Redis에 퍼블리시한 메시지: " + messageJson);
    }


}
