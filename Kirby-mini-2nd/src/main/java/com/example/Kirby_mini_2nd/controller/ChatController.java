package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import com.example.Kirby_mini_2nd.repository.entity.MessageType;
import com.example.Kirby_mini_2nd.service.ChatService;
import com.example.Kirby_mini_2nd.service.RedisPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final StringRedisTemplate redisTemplate;
    private final RedisPublisher redisPublisher;

    // 채팅방에 속한 사용자 수를 관리하기 위한 Map
    private final Map<Integer, Integer> roomUserCountMap = new ConcurrentHashMap<>();

    // 생성자 주입을 사용하여 의존성을 주입
    @Autowired
    public ChatController(ChatService chatService, StringRedisTemplate redisTemplate, RedisPublisher redisPublisher) {
        this.chatService = chatService;
        this.redisTemplate = redisTemplate;
        this.redisPublisher = redisPublisher;
    }

    @Autowired
    private ChannelTopic topic;

    // 전체 채팅방 출력
    @GetMapping("/roomList")
    public List<ChatRoom> getChatRooms() {
        return chatService.findAllRoom();
    }

    // 채팅방 입장
    @GetMapping("/room/{id}")
    public ChatRoom getChatRoomById(@PathVariable int id) {
        return chatService.getChatRoomById(id);
    }

    //채팅방 생성
    @PostMapping("/createRoom")
    public ChatRoom createRoom(@RequestBody ChatRoom chatRoom) {
        log.info("채팅방 생성 확인 : " + chatRoom);
        return chatService.createChatRoom(chatRoom);
    }

    // 메시지 전송 및 Redis에 저장.
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessage message) {
        try {
            // 메시지를 Redis에 퍼블리시
            redisTemplate.convertAndSend("chatroom", message.getMessage());
            System.out.println("Redis에 퍼블리시한 메시지: " + message.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Redis 퍼블리시 에러: " + e.getMessage());
        }
    }

    // 모든 메시지 불러오기 (DB + Redis)
    @GetMapping("/room/{roomId}/combinedMessages")
    public List<ChatMessage> getCombinedMessages(@PathVariable int roomId) {
        return chatService.getCombinedChatMessages(roomId);
    }

    // 채팅방 나가기
    @PostMapping("/room/{roomId}/leave")
    public void leaveChatRoom(@PathVariable int roomId, @RequestParam String userId) {
        log.info("{} 번 방에서 사용자 {} 가 나가기를 요청.", roomId, userId);

        // 나가기 메시지 생성
        ChatMessage leaveMessage = new ChatMessage();
        leaveMessage.setRoomId(roomId);
        leaveMessage.setSender(userId);
        leaveMessage.setMessage(userId + "님이 퇴장하였습니다.");
        leaveMessage.setType(MessageType.LEAVE);
        leaveMessage.setSendDateNow();

        // Redis를 통해 나가기 메시지 발행
        redisPublisher.publishMessage(topic, leaveMessage);

        // 퇴장한 사용자 수 처리
        int userCount = chatService.decreaseUserCount(roomId);
        if (userCount <= 0) {
            // 마지막 사용자가 나가는 경우 채팅방과 내역 삭제
            log.info("마지막 사용자가 나갔으므로 채팅방 {} 및 내역 삭제.", roomId);
            chatService.removeChatRoom(roomId);
            roomUserCountMap.remove(roomId);
        }
    }
}
