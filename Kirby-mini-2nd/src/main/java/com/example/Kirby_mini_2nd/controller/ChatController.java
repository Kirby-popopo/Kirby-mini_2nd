package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import com.example.Kirby_mini_2nd.repository.entity.MessageType;
import com.example.Kirby_mini_2nd.service.ChatService;
import com.example.Kirby_mini_2nd.service.MyWebSocketHandler;
import com.example.Kirby_mini_2nd.service.RedisPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final StringRedisTemplate redisTemplate;
    private final RedisPublisher redisPublisher;
    private final MyWebSocketHandler myWebSocketHandler;

    @Autowired
    private ChannelTopic topic;

    // 생성자 주입
    @Autowired
    public ChatController(ChatService chatService, StringRedisTemplate redisTemplate, RedisPublisher redisPublisher, MyWebSocketHandler myWebSocketHandler) {
        this.chatService = chatService;
        this.redisTemplate = redisTemplate;
        this.redisPublisher = redisPublisher;
        this.myWebSocketHandler = myWebSocketHandler;
    }

    // 채팅방 리스트 조회 (사용자가 나간 방 제외)
    @GetMapping("/roomList")
    public List<ChatRoom> getRoomList(@RequestParam(required = false) String userId) {
        if (userId == null || userId.isEmpty()) {
            log.warn("userId가 전달되지 않았습니다. 기본 처리 수행.");
            return chatService.findAllRoom(); // 모든 채팅방 반환
        }
        log.info("사용자 {} 에 대한 채팅방 목록 요청", userId);
        return chatService.getAvailableRooms(userId);
    }

    // 채팅방 입장
    @GetMapping("/room/{id}")
    public ChatRoom getChatRoomById(@PathVariable int id) {
        return chatService.getChatRoomById(id);
    }

    // 채팅방 생성
    @PostMapping("/createRoom")
    public ChatRoom createRoom(@RequestBody ChatRoom chatRoom) {
        log.info("채팅방 생성 확인: {}", chatRoom);
        return chatService.createChatRoom(chatRoom);
    }

    // 메시지 전송 (Redis에 퍼블리시)
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessage message) {
        try {
            redisTemplate.convertAndSend("chatroom", message.getMessage());
            log.info("Redis에 퍼블리시한 메시지: {}", message.getMessage());
        } catch (Exception e) {
            log.error("Redis 퍼블리시 에러: {}", e.getMessage());
        }
    }

    // 채팅방 메시지 가져오기 (DB + Redis 결합)
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

        // Redis에 나가기 메시지 발행
        redisPublisher.publishMessage(topic, leaveMessage);

        // 사용자가 명시적으로 나가기 요청한 세션에 플래그 설정
        WebSocketSession session = myWebSocketHandler.getSessionForUser(userId, roomId);
        if (session != null) {
            myWebSocketHandler.markExplicitLeave(session);
        }

        // 사용자가 나간 방 정보 기록
        chatService.recordUserExit(userId, roomId);

        // 현재 사용자 수 감소
        int userCount = chatService.decreaseUserCount(roomId);
        log.info("채팅방 {} 의 현재 사용자 수: {}", roomId, userCount);

        if (userCount == 0) {
            // 마지막 사용자가 나가는 경우 방 삭제
            log.info("마지막 사용자가 나갔으므로 채팅방 {} 및 내역 삭제.", roomId);
            chatService.removeChatRoom(roomId);
        }
    }

    // 특정 채팅방의 메시지 가져오기 (페이징 지원)
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<Page<ChatMessage>> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page, // 기본 0번째 페이지
            @RequestParam(defaultValue = "20") int size // 기본 페이지 크기 20
    ) {
        Page<ChatMessage> messages = chatService.getMessagesByRoomId(roomId, page, size);
        return ResponseEntity.ok(messages);
    }

}