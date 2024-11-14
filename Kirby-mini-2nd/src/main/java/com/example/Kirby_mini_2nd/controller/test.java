package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.User;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class test {
    UserRepo userRepo; // 삭제

    public test(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @PostMapping("/getUser")
    public ResponseEntity<ResponseModel> getUserInfo(@RequestBody Map<String, String> requestData){
        String userId = requestData.get("id");

        return ResponseModel.MakeResponse(userRepo.findByUserId(userId).get(), HttpStatus.OK);
    }
}
