package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.dto.UserDTO;
import com.example.Kirby_mini_2nd.repository.entity.ChatMessage;
import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import com.example.Kirby_mini_2nd.repository.entity.UserChatExit;
import com.example.Kirby_mini_2nd.repository.entity.User;

import com.example.Kirby_mini_2nd.repository.repo.ChatMessageRepository;
import com.example.Kirby_mini_2nd.repository.repo.ChatRoomRepository;
import com.example.Kirby_mini_2nd.repository.repo.UserChatExitRepository;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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

    @Autowired
    private UserChatExitRepository userChatExitRepository;

    @Autowired
    private UserRepo userRepo;

    private final RedisTemplate<String, Object> redisTemplate;
    private final Gson gson;
    private final Map<Integer, Integer> roomUserCountMap = new ConcurrentHashMap<>();

    @Autowired
    public ChatService(RedisTemplate<String, Object> redisTemplate, Gson gson) {
        this.redisTemplate = redisTemplate;
        this.gson = gson;
    }

    // 채팅방 리스트 (나간 방 제외)
    public List<ChatRoom> getNonExitedRooms (String userId) {
        // 나간 방 ID 조회
        List<Integer> exitedRoomIds = userChatExitRepository.findExitedRoomIdsByUserId(userId);

        // 나간 방 제외한 채팅방 리스트 반환
        if (exitedRoomIds.isEmpty()) {
            return chatRoomRepository.findAll();
        }
        return chatRoomRepository.findAllByRoomIdNotIn(exitedRoomIds);
    }

    // 사용자가 나간 방 기록
    public void saveUserExitRecord(String userId, int roomId) {
        // 사용자가 이미 나간 방이 아닌 경우만 저장
        if (!userChatExitRepository.existsByRoomIdAndUserId(roomId, userId)) {
            userChatExitRepository.save(new UserChatExit(userId, roomId));
            log.info("사용자 {} 가 채팅방 {} 에서 나간 기록이 저장되었습니다.", userId, roomId);
        }
    }

    // 전체 채팅방 출력
    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll();
    }

    // 특정 채팅방 출력
    public ChatRoom getChatRoomById(int roomId) {
        return chatRoomRepository.findById(roomId).orElse(null);
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    // Redis에 메시지 저장
    public void saveMessageToRedis(String key, ChatMessage message) {
        redisTemplate.opsForList().rightPush(key, gson.toJson(message));
        log.info("Redis에 메시지 저장 완료: {}", message);
    }

    // MySQL & Redis 결합된 모든 메시지 조회
    public List<ChatMessage> getCombinedChatMessages(int roomId) {
        // MySQL에서 메시지 조회
        List<ChatMessage> dbMessages = chatMessageRepository.findByRoomId(roomId, Sort.by(Sort.Direction.ASC, "sendDate"));

        // Redis에서 메시지 조회
        String key = "chatRoom:" + roomId;
        List<Object> redisMessagesRaw = redisTemplate.opsForList().range(key, 0, -1);
        List<ChatMessage> redisMessages = new ArrayList<>();

        if (redisMessagesRaw != null) {
            for (Object messageJson : redisMessagesRaw) {
                if (messageJson instanceof String) {
                    try {
                        ChatMessage chatMessage = gson.fromJson((String) messageJson, ChatMessage.class);
                        redisMessages.add(chatMessage);
                    } catch (Exception e) {
                        log.error("Redis 메시지 파싱 오류: {}", e.getMessage());
                    }
                }
            }
        }

        // Redis 메시지를 시간순으로 정렬
        redisMessages.sort(Comparator.comparing(msg -> OffsetDateTime.parse(msg.getSendDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        // DB 메시지와 Redis 메시지 통합
        List<ChatMessage> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(dbMessages);
        combinedMessages.addAll(redisMessages);

        return combinedMessages;
    }

    // 채팅방 삭제 및 메시지 삭제
    @Transactional
    public void removeChatRoom(int roomId) {
        // MySQL 채팅방 삭제
        if (chatRoomRepository.existsById(roomId)) {
            chatRoomRepository.deleteById(roomId);
            log.info("채팅방 {} 이 MySQL에서 삭제되었습니다.", roomId);
        }

        // MySQL 메시지 삭제
        chatMessageRepository.deleteAllByRoomId(roomId);

        // Redis 메시지 삭제
        String key = "chatRoom:" + roomId;
        redisTemplate.delete(key);
        log.info("Redis에서 채팅방 {} 의 메시지가 삭제되었습니다.", roomId);
    }

    // 채팅방 사용자 수 증가
    public int increaseUserCount(int roomId) {
        return roomUserCountMap.merge(roomId, 1, Integer::sum);
    }

    // 채팅방 사용자 수 감소
    public int decreaseUserCount(int roomId) {
        Integer currentCount = roomUserCountMap.get(roomId);
        if (currentCount == null || currentCount <= 1) {
            roomUserCountMap.remove(roomId);
            return 0;
        }
        roomUserCountMap.put(roomId, currentCount - 1);
        return currentCount - 1;
    }

    // 특정 채팅방의 사용자 수 조회
    public int getUserCount(int roomId) {
        return roomUserCountMap.getOrDefault(roomId, 0);
    }

    // Redis 메시지를 MySQL로 주기적으로 마이그레이션
    @Scheduled(fixedRate = 600000) // 10분 간격
    public void migrateMessagesToMySQL() {
        Set<String> keys = redisTemplate.keys("chatRoom:*");
        if (keys == null || keys.isEmpty()) {
            log.info("Redis에 저장된 메시지가 없어 마이그레이션을 건너뜁니다.");
            return;
        }

        List<ChatMessage> chatMessagesToSave = new ArrayList<>();
        for (String key : keys) {
            List<Object> messageJsonList = redisTemplate.opsForList().range(key, 0, -1);
            if (messageJsonList != null) {
                for (Object messageJson : messageJsonList) {
                    if (messageJson instanceof String) {
                        ChatMessage chatMessage = gson.fromJson((String) messageJson, ChatMessage.class);
                        chatMessagesToSave.add(chatMessage);
                    }
                }
            }
        }

        if (!chatMessagesToSave.isEmpty()) {
            chatMessageRepository.saveAll(chatMessagesToSave);
            redisTemplate.delete(keys);
            log.info("Redis에서 MySQL로 {}개의 메시지를 마이그레이션했습니다.", chatMessagesToSave.size());
        }
    }

    public Page<ChatMessage> getMessagesByRoomId(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sendDate"));
        return chatMessageRepository.findByRoomId(roomId, pageable);
    }

    public int findOrCreateRoomByExactParticipants(List<String> participantIds, String thisUserId) {
        // 현재 유저 ID 제외한 다른 유저 ID 가져오기
        List<String> otherUserIds = participantIds.stream()
                .filter(id -> !id.equals(thisUserId)) // 현재 사용자 ID 제외
                .collect(Collectors.toList());

        // 상대방 이름 가져오기
        List<String> otherUserNames = userRepo.findAllById(otherUserIds).stream()
                .map(User::getName) // User 엔티티에서 이름 가져오기
                .collect(Collectors.toList());

        // 채팅방 이름 설정
        String roomName = otherUserNames.size() == 1
                ? otherUserNames.get(0)
                : String.join(", ", otherUserNames);

        // 새로운 채팅방 생성
        ChatRoom newRoom = new ChatRoom();
        newRoom.setParticipants(new HashSet<>(participantIds));
        newRoom.setRoomName(roomName);
        chatRoomRepository.save(newRoom);

        return newRoom.getRoomId();
    }


    public List<UserDTO> getParticipantsByRoomId(int roomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 참여자 ID 추출
        Set<String> participantIds = chatRoom.getParticipants();

        // 참여자 ID를 기반으로 User 엔티티 조회 및 UserDTO로 변환
        return userRepo.findAllById(participantIds).stream()
                .map(user -> new UserDTO(
                        user.getUserId(),        // userId 필드의 getter 메서드
                        user.getName(),          // name 필드의 getter 메서드
                        user.getProfileImage()   // profileImage 필드의 getter 메서드
                ))
                .collect(Collectors.toList());
    }


    public List<ChatMessage> getPreviousMessages(int roomId, String before, int limit) {
        // ISO 8601 형식의 UTC 시간 파싱
        LocalDateTime beforeDateTime = Instant.parse(before)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Repository 호출
        List<ChatMessage> messages = chatMessageRepository.findMessagesBefore(roomId, beforeDateTime);
        return messages.stream().limit(limit).collect(Collectors.toList());
    }


    // 채팅방 찾기 - 특정 참가자 목록에 맞는 방이 있는지 검색
    public Optional<ChatRoom> findRoomByParticipants(List<String> participantIds, String currentUserId) {
        Set<String> allParticipants = new HashSet<>(participantIds);
        allParticipants.add(currentUserId);

        // 모든 방을 검색하여 참가자 목록이 일치하는 방을 찾음
        List<ChatRoom> allRooms = chatRoomRepository.findAll(); // 가능한 최적화를 위해 더 나은 쿼리가 필요할 수 있음
        for (ChatRoom room : allRooms) {
            if (room.getParticipants().equals(allParticipants)) {
                return Optional.of(room);
            }
        }

        // 일치하는 방이 없는 경우 Optional.empty() 반환
        return Optional.empty();
    }

    // 새로운 채팅방 생성
    public ChatRoom createRoom(List<String> participantIds, String currentUserId) {
        // 참여자 목록 중 현재 사용자가 아닌 상대방을 찾기
        String otherParticipantId = participantIds.stream()
                .filter(id -> !id.equals(currentUserId))
                .findFirst()
                .orElse("Unknown User");

        // 상대방의 이름을 가져오기 위해 userRepo 사용
        String otherParticipantName = userRepo.findById(otherParticipantId)
                .map(User::getName)
                .orElse("Unknown User");

        // 채팅방 이름을 상대방 이름으로 설정
        String roomName = otherParticipantName;

        // 새로운 ChatRoom 객체 생성
        Set<String> participants = new HashSet<>(participantIds);
        ChatRoom newRoom = new ChatRoom(roomName, participants);

        // 새로운 채팅방 저장
        return chatRoomRepository.save(newRoom);
    }


}
