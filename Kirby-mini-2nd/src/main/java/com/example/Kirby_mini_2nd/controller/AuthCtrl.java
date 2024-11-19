package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.User;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import com.example.Kirby_mini_2nd.service.AuthSvc;
import com.example.Kirby_mini_2nd.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthCtrl {

    @Autowired
    AuthSvc authSvc;

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseModel> signup(@RequestBody User user) {
//        String userId = requestData.get("userId");
//        String userPw = requestData.get("userPw");


        try {
            return ResponseModel.MakeResponse(authSvc.signUp(user), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseModel.MakeResponse("signup Error?", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> params,
                                        HttpServletResponse res) {

        User user = userRepo.findByUserIdAndUserPw(params.get("userId"), params.get("userPw"));

        log.info("request userId: {}", params.get("userId"));
        log.info("login 실행");
        if (user != null) {
            String id = user.getUserId();
            String token = jwtService.getToken("id", id);
            MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            header.add("Authorization", token);
            return new ResponseEntity<>("로그인 성공", header, HttpStatus.OK);
            // authorization
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token, HttpServletResponse res) {
        log.info("logout 실행");
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "delete");
        return new ResponseEntity<>("로그아웃 성공", header, HttpStatus.OK);
    }


    // 로그인 확인
    @GetMapping("/check")
    public ResponseEntity check(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("check controller : "+token);
        Claims claims = jwtService.getClaims(token);

        if (claims != null) {
            String id = claims.get("id").toString();
            User user = userRepo.findById(id).get();
            return new ResponseEntity<>("반가워요.. "+user.getUserId()+" 회원님!!", HttpStatus.OK);
        }
        return new ResponseEntity<>("로그인을 먼저 수행하세요~~", HttpStatus.OK);
    }
}

