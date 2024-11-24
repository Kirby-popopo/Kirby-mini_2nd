package com.example.Kirby_mini_2nd.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id; // 사용자 ID
    private String name; // 사용자 이름
    private String profileImageUrl; // 프로필 이미지 URL
}
