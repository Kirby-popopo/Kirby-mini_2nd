package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.config.GsonConfig;
import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.example.Kirby_mini_2nd.repository.entity.MessageType;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatService chatService;

    @Autowired
    private RedisPublisher redisPublisher;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    // 모든 WebSocket 세션 관리
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final Map<WebSocketSession, Integer> sessionRoomMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Boolean> explicitLeaveFlag = new ConcurrentHashMap<>();

    private final Gson gson = GsonConfig.getGson(); // Gson 인스턴스 생성

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        try {
            // URI에서 방 ID 추출
            String uri = session.getUri().toString();
            log.info("WebSocket 연결 시도: URI = {}", uri);

            String[] uriSegments = uri.split("/");
            String roomIdString = uriSegments[uriSegments.length - 1];

            if (!roomIdString.matches("\\d+")) {
                throw new IllegalArgumentException("유효하지 않은 방 ID입니다: " + roomIdString);
            }

            int roomId = Integer.parseInt(roomIdString);
            sessionRoomMap.put(session, roomId); // 세션에 방 ID 매핑

            // 사용자 수 증가
            int userCount = chatService.increaseUserCount(roomId);
            log.info("새 세션 연결: {}, Room ID: {}, 현재 사용자 수: {}", session.getId(), roomId, userCount);

        } catch (Exception e) {
            log.error("WebSocket 연결 중 에러 발생: {}", e.getMessage());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("수신된 메시지: {}", message.getPayload());

        try {
            // JSON 메시지를 객체로 변환
            ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
            chatMessage.setType(MessageType.TALK); // 메시지 타입 설정
            redisPublisher.publishAndSaveMessage(topic, chatMessage); // 메시지 발행
        } catch (Exception e) {
            log.error("메시지 파싱 오류: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("세션 종료: {}", session.getId());

        Integer roomId = sessionRoomMap.get(session);

        if (roomId != null) {
            // 명시적 나가기 확인
            if (!isExplicitLeave(session)) {
                log.info("뒤로가기 또는 비정상 종료로 인해 방을 나가지 않음: Room ID: {}", roomId);
                return;
            }

            // 사용자 수 감소
            int userCount = chatService.decreaseUserCount(roomId);

            if (userCount > 0) {
                // 사용자 퇴장 메시지 생성 및 발행
                ChatMessage leaveMessage = new ChatMessage();
                leaveMessage.setType(MessageType.LEAVE);
                leaveMessage.setMessage(session.getId() + "님이 퇴장하였습니다.");
                leaveMessage.setRoomId(roomId);
                leaveMessage.setSender("System");
                redisPublisher.publishMessage(topic, leaveMessage);
            } else {
                // 마지막 사용자 퇴장 시 채팅방 삭제
                log.info("마지막 사용자가 나가므로 채팅방 {} 삭제", roomId);
                chatService.removeChatRoom(roomId);
                redisTemplate.delete("chatRoom:" + roomId);
            }

            // 세션 매핑 정보 제거
            sessionRoomMap.remove(session);
        }
    }

    // 명시적으로 나가기 플래그 설정
    public void markExplicitLeave(WebSocketSession session) {
        explicitLeaveFlag.put(session, true);
    }

    // 명시적으로 나갔는지 확인
    public boolean isExplicitLeave(WebSocketSession session) {
        return explicitLeaveFlag.getOrDefault(session, false);
    }

    // 사용자와 연결된 WebSocket 세션 검색
    public WebSocketSession getSessionForUser(String userId, int roomId) {
        for (WebSocketSession session : sessions) {
            String sessionUserId = (String) session.getAttributes().get("userId");
            Integer sessionRoomId = sessionRoomMap.get(session);

            if (userId.equals(sessionUserId) && roomId == sessionRoomId) {
                return session;
            }
        }
        return null;
    }

    // Redis 메시지를 모든 세션에 브로드캐스트
    public void broadcastMessage(String messageJson) {
        log.info("Redis로부터 수신된 메시지를 브로드캐스트: {}", messageJson);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(messageJson));
                } catch (IOException e) {
                    log.error("메시지 전송 실패: {}", e.getMessage());
                }
            }
        }
    }
}
