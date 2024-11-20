package com.example.Kirby_mini_2nd.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchUserDTO {

    private String userId;
    private String profileImage;
    private String name;

    public SearchUserDTO(String userId, String profileImage, String name) {
        this.userId = userId;
        this.profileImage = profileImage;
        this.name = name;
    }
}
