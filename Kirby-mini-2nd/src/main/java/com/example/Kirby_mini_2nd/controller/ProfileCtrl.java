package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.model.dto.ProfileDTO;
import com.example.Kirby_mini_2nd.model.vo.ProfileVO;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import com.example.Kirby_mini_2nd.service.FollowSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/*
*  유저 관련 서비스 생성시 현재 유저 관련 코드 삭제
* */
@RestController
@CrossOrigin(origins = "*")
public class ProfileCtrl {
    FollowSvc followSvc;
    UserRepo userRepo; // 삭제

    @Autowired
    public ProfileCtrl(FollowSvc followSvc, UserRepo userRepo){
        this.followSvc = followSvc;
        this.userRepo = userRepo; // 삭제
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
}
