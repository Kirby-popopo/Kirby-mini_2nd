package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.config.GsonConfig;
import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.example.Kirby_mini_2nd.repository.entity.MessageType;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

    // 모든 세션을 관리하기 위한 세트
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final Gson gson = GsonConfig.getGson(); // Gson 인스턴스를 가져옴
    private final Map<WebSocketSession, Integer> sessionRoomMap = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        try {
            // WebSocket 세션의 URI에서 방 ID 추출
            String uri = session.getUri().toString();
            log.info("WebSocket 연결 시도: URI = {}", uri);

            // URI 예시: ws://localhost:8090/ws/{roomId}
            String[] uriSegments = uri.split("/");
            String roomIdString = uriSegments[uriSegments.length - 1]; // 마지막 부분이 방 ID라고 가정

            // 방 ID 추출 시 유효성 검사
            if (!roomIdString.matches("\\d+")) { // 방 ID가 숫자가 아닐 경우
                throw new IllegalArgumentException("유효하지 않은 방 ID입니다: " + roomIdString);
            }

            int roomId = Integer.parseInt(roomIdString);
            sessionRoomMap.put(session, roomId); // 세션에 방 ID 매핑

            // 채팅방 사용자 수 증가
            int userCount = chatService.increaseUserCount(roomId);
            log.info("New session connected: {}, Room ID: {}, Current User Count: {}", session.getId(), roomId, userCount);

        } catch (NumberFormatException e) {
            log.error("방 ID를 파싱하는데 실패했습니다. URI: {}", session.getUri(), e);
            session.close(CloseStatus.BAD_DATA); // 잘못된 데이터로 인해 세션을 종료
        } catch (IllegalArgumentException e) {
            log.error("잘못된 방 ID입니다: {}", session.getUri(), e);
            session.close(CloseStatus.BAD_DATA); // 잘못된 데이터로 인해 세션을 종료
        }
    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received message: " + message.getPayload());

        // Gson을 사용하여 JSON 문자열을 ChatMessage 객체로 변환
        try {
            ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
            chatMessage.setType(MessageType.TALK); // TALK 타입 설정
            System.out.println("채팅 메시지 역직렬화 성공: " + chatMessage);

            // Redis Publisher를 통해 발행
            redisPublisher.publishAndSaveMessage(topic, chatMessage);
        } catch (Exception e) {
            System.err.println("메시지 파싱 오류: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Session closed: " + session.getId());

        // 세션에 매핑된 방 ID를 가져옵니다.
        Integer roomId = sessionRoomMap.get(session);

        if (roomId != null) {
            // 채팅방 사용자 수 감소
            int userCount = chatService.decreaseUserCount(roomId);

            // 사용자 수가 0 이상이면 퇴장 메시지 전송
            if (userCount > 0) {
                // 퇴장(LEAVE) 메시지 생성 및 전송
                ChatMessage leaveMessage = new ChatMessage();
                leaveMessage.setType(MessageType.LEAVE);
                leaveMessage.setMessage(session.getId() + "님이 퇴장하였습니다.");
                leaveMessage.setRoomId(roomId);
                leaveMessage.setSender("System");

                // Redis에 발행 (저장하지 않음)
                redisPublisher.publishMessage(topic, leaveMessage);
                System.out.println("사용자 퇴장 메시지 발행: Room ID: " + roomId + ", Session ID: " + session.getId());
            } else {
                // 마지막 사용자가 나가는 경우 채팅방과 내역 삭제
                System.out.println("마지막 사용자가 나갔으므로 채팅방: " + roomId + " 삭제");
                chatService.removeChatRoom(roomId);
                redisTemplate.delete("chatRoom:" + roomId);
            }
            // 세션 매핑 정보 제거
            sessionRoomMap.remove(session);
        }
    }

    // Redis에서 수신된 메시지를 연결된 모든 클라이언트에게 브로드캐스트
    public void broadcastMessage(String messageJson) {
        System.out.println("WebSocket으로 메시지 브로드캐스트: " + messageJson);
        for (WebSocketSession sess : sessions) {
            if (sess.isOpen()) {
                try {
                    sess.sendMessage(new TextMessage(messageJson));
                    System.out.println("메시지를 클라이언트로 전송: " + sess.getId());
                } catch (IOException e) {
                    System.err.println("메시지 전송 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
