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
    private String gender;

    public ProfileDTO(String name, String profileImage, String description, String gender) {
        this.name = name;
        this.profileImage = profileImage;
        this.description = description;
        this.gender = gender;
    }
}
