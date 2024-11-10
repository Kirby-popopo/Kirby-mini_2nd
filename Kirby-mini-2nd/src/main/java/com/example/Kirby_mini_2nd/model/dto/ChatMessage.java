package com.example.Kirby_mini_2nd.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage implements Serializable {

    private MessageType type; // 메시지 타입
    private String message; // 메시지

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime sendDate; // 시간대 정보를 포함한 OffsetDateTime

    private int roomId; // 발송지인 채팅방 (FK), 방 번호
    private String sender; // 발송자

    private String formattedSendDate; // 포맷된 날짜 또는 시간 출력용
    private String displayDate; // 메시지를 출력할 때 사용할 날짜 또는 시간

    // 한국 시간대로 변환하여 포맷팅된 날짜를 반환하는 메서드
    public String getFormattedSendDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. M. d. a h:mm").withLocale(Locale.KOREA);
        // sendDate를 한국 시간대로 변환하여 포맷팅
        return sendDate.withOffsetSameInstant(ZoneOffset.ofHours(9)).format(formatter);
    }
}
