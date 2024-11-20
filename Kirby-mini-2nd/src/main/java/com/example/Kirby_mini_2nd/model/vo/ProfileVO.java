package com.example.Kirby_mini_2nd.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileVO {
    private String userName;
    private String userProfileImage;
    private String userDescription;
    private String userGender;
    private List<String> following;
    private List<String> follower;

    public ProfileVO(String name, String profileImage,
                     String userDescription, List<String> follower,
                     List<String> following
                     ){
        this.userName = name;
        this.userProfileImage = profileImage;
        this.userDescription = userDescription;
        this.follower = follower;
        this.following = following;
    }

    public ProfileVO(String name,
                     String profileImage,
                     String userDescription,
                     String gender
    ){
        this.userName = name;
        this.userProfileImage = profileImage;
        this.userDescription = userDescription;
        this.userGender = gender;
    }
}
