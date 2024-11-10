package com.example.Kirby_mini_2nd.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfileDTO {
    private String name;
    private String profileImage;
    private String description;

    public ProfileDTO(String name, String profileImage, String description) {
        this.name = name;
        this.profileImage = profileImage;
        this.description = description;
    }
}
