package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import com.example.Kirby_mini_2nd.repository.repo.ChatMessageRepository;
import com.example.Kirby_mini_2nd.repository.repo.ChatRoomRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private final RedisTemplate<String, Object> redisTemplate;  // RedisTemplate의 타입을 Object로 변경
    private final Gson gson;
    private final Map<Integer, Integer> roomUserCountMap = new ConcurrentHashMap<>();

    @Autowired
    public ChatService(RedisTemplate<String, Object> redisTemplate, Gson gson) {
        this.redisTemplate = redisTemplate;
        this.gson = gson;
    }

    // 전체 채팅방 출력
    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll();
    }

    // 특정 채팅방 출력
    public ChatRoom getChatRoomById(int roomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(roomId);
        return chatRoom.orElse(null); // 존재하지 않는다면 null 반환
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    // Redis에 메시지 저장 메서드 (추가적으로 Redis 저장 확인을 위해 구현 가능)
    public void saveMessageToRedis(String key, ChatMessage message) {
        redisTemplate.opsForList().rightPush(key, message);
        log.info("Redis에 메시지 저장 완료: {}", message);
    }

    // 스케줄링 작업 & 모든 메시지를 MySQL에 저장
    @Scheduled(fixedRate = 6000000) // 1분
    public void migrateMessageToMySQL() {
        // Redis에서 특정 패턴의 모든 키를 가져오기
        Set<String> keys = redisTemplate.keys("chatRoom:*");

        if (keys == null || keys.isEmpty()) {
            log.info("Redis에 저장된 메시지가 없습니다. 마이그레이션을 건너뜁니다.");
            return;
        }

        List<ChatMessage> chatMessagesToSave = new ArrayList<>();

        for (String key : keys) {
            List<Object> messageJsonList = redisTemplate.opsForList().range(key, 0, -1);

            if (messageJsonList != null) {
                for (Object messageJson : messageJsonList) {
                    if (messageJson instanceof String) {
                        // Gson 이용해서 JSon 문자열 ChatMessage 객체로 변환
                        ChatMessage chatMessage = gson.fromJson((String) messageJson, ChatMessage.class);
                        chatMessagesToSave.add(chatMessage);
                    }
                }
            }
        }

        if (!chatMessagesToSave.isEmpty()) {
            chatMessageRepository.saveAll(chatMessagesToSave);
            log.info("Redis에서 MySQL로 메시지 마이그레이션 완료: 총 {} 개의 메시지", chatMessagesToSave.size());
            // 마이그레이션 후 Redis 데이터 삭제
            redisTemplate.delete(keys);
        } else {
            log.info("저장할 메시지가 없습니다.");
        }
    }

    // MySQL & Redis에 저장되어 있는 모든 채팅 내역 불러오기
    public List<ChatMessage> getCombinedChatMessages(int roomId) {
        // MySQL에서 과거 메시지 가져오기 (채팅방 ID별로 정렬)
        List<ChatMessage> dbMessages = chatMessageRepository.findByRoomId(roomId, Sort.by(Sort.Direction.ASC, "sendDate"));

        // Redis에서 현재 저장된 최신 메시지 가져오기
        String key = "chatRoom:" + roomId;
        List<Object> redisMessageList = redisTemplate.opsForList().range(key, 0, -1);
        List<ChatMessage> redisMessages = new ArrayList<>();

        if (redisMessageList != null) {
            for (Object messageJson : redisMessageList) {
                if (messageJson instanceof String) {
                    try {
                        // Gson을 사용하여 JSON 문자열을 ChatMessage 객체로 변환
                        ChatMessage chatMessage = gson.fromJson((String) messageJson, ChatMessage.class);
                        redisMessages.add(chatMessage);
                    } catch (Exception e) {
                        log.error("Redis 메시지 파싱 중 오류 발생: {}", e.getMessage());
                    }
                }
            }
        }

        // Redis에서 가져온 메시지 시간순으로 정렬
        redisMessages.sort(Comparator.comparing(msg -> OffsetDateTime.parse(msg.getSendDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // MySQL 메시지와 Redis 메시지를 하나의 리스트로 통합
        List<ChatMessage> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(dbMessages);
        combinedMessages.addAll(redisMessages);

        // 최종적으로 통합된 메시지 반환 (이미 MySQL에서 정렬된 상태 + Redis에서 시간순 정렬된 상태)
        return combinedMessages;
    }

    // 채팅방 삭제 및 채팅 내역 삭제.
    @Transactional
    public void removeChatRoom(int roomId) {
        // MySQL 에서 채팅방 삭제
        if (chatRoomRepository.existsById(roomId)) {
            chatRoomRepository.deleteById(roomId);
            log.info("채팅방 {} 이 MySQL에서 삭제되었습니다.", roomId);
        } else {
            log.info("채팅방 {} 은 존재하지 않습니다.", roomId);
        }

        // MySQL에서 채팅 내역 삭제
        chatMessageRepository.deleteAllByRoomId(roomId);

        // Redis에서 채팅 내역 삭제
        removeChatMessagesFromRedis(roomId);
    }

    public void removeChatMessagesFromRedis(int roomId) {
        String key = "chatRoom:" + roomId;
        redisTemplate.delete(key);
        log.info("채팅방 {} 의 모든 채팅 메시지가 Redis에서 삭제되었습니다.", roomId);
    }

    public int increaseUserCount(int roomId) {
        return roomUserCountMap.merge(roomId, 1, Integer::sum);
    }

    public int decreaseUserCount(int roomId) {
        Integer currentUserCount = roomUserCountMap.get(roomId);

        if (currentUserCount != null) {
            if (currentUserCount <= 1) {
                roomUserCountMap.remove(roomId);
                return 0;  // 마지막 사용자가 나가면 0 반환
            } else {
                roomUserCountMap.put(roomId, currentUserCount - 1);
                return currentUserCount - 1;  // 감소한 사용자 수 반환
            }
        }
        return 0;
    }

    // 특정 채팅방의 사용자 수 카운트.
    public int getUserCount(int roomId) {
        return roomUserCountMap.getOrDefault(roomId, 0);
    }

    // 특정 채팅방 정보 제거.
    public void removeRoom(int roomId) {
        roomUserCountMap.remove(roomId);
    }
}
