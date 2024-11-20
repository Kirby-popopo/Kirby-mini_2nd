package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.model.dto.ProfileDTO;
import com.example.Kirby_mini_2nd.model.vo.ProfileVO;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import com.example.Kirby_mini_2nd.service.FollowSvc;
import com.example.Kirby_mini_2nd.service.PostSvc;
import com.example.Kirby_mini_2nd.service.ProfileSvc;
import jakarta.annotation.Nullable;
import com.example.Kirby_mini_2nd.service.ProfileSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
*  유저 관련 서비스 생성시 현재 유저 관련 코드 삭제
* */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ProfileCtrl {
    FollowSvc followSvc;
    ProfileSvc profileSvc;
    UserRepo userRepo; // 삭제

    @Autowired
    public ProfileCtrl(FollowSvc followSvc, UserRepo userRepo, ProfileSvc profileSvc){
        this.followSvc = followSvc;
        this.profileSvc = profileSvc;
        this.userRepo = userRepo; // 삭제
    }

    @PostMapping("/GetUser")
    public ResponseEntity<ResponseModel> GetUserProfile(@RequestBody Map<String, String> requestData){
        String userId = requestData.get("userId");

        ProfileDTO userProfileDTO = userRepo.findByUserIdForProfile(userId);// 삭제

        ProfileVO userProfileVO = new ProfileVO(
                userProfileDTO.getName(),
                userProfileDTO.getProfileImage(),
                userProfileDTO.getDescription(),
                userProfileDTO.getGender()
        );

        return ResponseModel.MakeResponse(userProfileVO, HttpStatus.OK);
    }

    @PostMapping("/Profile")
    public ResponseEntity<ResponseModel> AllUserProfile(@RequestBody Map<String, String> requestData){
        String userId = requestData.get("userId");

        List<String> follower = followSvc.SearchFollower(userId);
        List<String> following = followSvc.SearchFollowing(userId);
        ProfileDTO userProfileDTO = userRepo.findByUserIdForProfile(userId);// 삭제

        ProfileVO userProfileVO = new ProfileVO(
                userProfileDTO.getName(),
                userProfileDTO.getProfileImage(),
                userProfileDTO.getDescription(),
                follower,
                following
                );

        return ResponseModel.MakeResponse(userProfileVO, HttpStatus.OK);
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<ResponseModel> UpdateProfile(
            @Nullable @RequestParam("updateImage") MultipartFile file,
            @RequestParam("updateImageName") String fileName,
            @RequestParam("bio") String bio,
            @RequestParam("gender") String gender,
            @RequestParam("id") String id)
    {
        String result = profileSvc.updateProfile(id, file, fileName, bio, gender);
        if (result.equals("UpdateError")){
            return ResponseModel.MakeResponse("fail", HttpStatus.OK);
        }
        return ResponseModel.MakeResponse("good", HttpStatus.OK);
    }
}
