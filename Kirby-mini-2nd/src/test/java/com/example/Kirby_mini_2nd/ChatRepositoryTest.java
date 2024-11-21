package com.example.Kirby_mini_2nd;

import com.example.Kirby_mini_2nd.repository.entity.ChatRoom;
import com.example.Kirby_mini_2nd.repository.repo.ChatRoomRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
public class ChatRepositoryTest {
    @Autowired
    ChatRoomRepository repository;

    @Test
    void findAll() { // 채팅방 리스트 출력
        List<ChatRoom> list = repository.findAll();
        list.forEach(System.out::println);
    }

    @Test
    void findByRoomId() {
        Optional<ChatRoom> chatRoom = repository.findById(1);
        if (chatRoom.isEmpty()) {
            System.out.println("존재하지 않음");
        } else {
            System.out.println(chatRoom.get());
        }
    }

//    @Test
//    @Rollback(false)
//    void insert() {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setRoom_name("1102 새로운 채팅방");
//        repository.save(chatRoom);
//        System.out.println("insert 확인"+chatRoom);
//    }
}
