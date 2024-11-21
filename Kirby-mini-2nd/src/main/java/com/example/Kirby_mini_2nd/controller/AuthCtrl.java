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
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
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

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<ResponseModel> signup(@RequestBody User user) {

        try {
            return ResponseModel.MakeResponse(authSvc.signUp(user), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseModel.MakeResponse("signup Error?", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> params) {

        String userId = params.get("userId");
        String userPw = params.get("userPw");

        User user = userRepo.findByUserId(userId);

        log.info("request userId: {}", params.get("userId"));
        log.info("login 실행");
        if (passwordEncoder.matches(userPw, user.getUserPw())) {

            String token = jwtService.getToken("id", user.getUserId());

            HttpHeaders headers = new HttpHeaders();
            headers.add("authorization", "Bearer" + token);
            return new ResponseEntity<>("로그인 성공", headers, HttpStatus.OK);
            // authorization
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다.");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "authorization", required = false) String token, HttpServletResponse res) {
        log.info("logout 실행");
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("authorization", "delete");
        return new ResponseEntity<>("로그아웃 성공", header, HttpStatus.OK);
    }


    // 로그인 확인
    @GetMapping("/check")
    public ResponseEntity check(@RequestHeader(value = "authorization", required = false) String token) {
        log.info("check controller : "+token);
        Claims claims = jwtService.getClaims(token);

        if (claims != null) {
            String id = claims.get("id").toString();
            User user = userRepo.findById(id).get();
            return new ResponseEntity<>("반가워요.. "+user.getUserId()+" 회원님!!", HttpStatus.OK);
        }
        return new ResponseEntity<>("로그인을 먼저 수행하세요~~", HttpStatus.UNAUTHORIZED);
    }

    // 아이디 중복 확인
    @GetMapping("/checkUserId")
    public ResponseEntity<Boolean> checkUserIdAvailability(@RequestParam String userId){
        int count = userRepo.countByUserId(userId);
        boolean isAvailable = count == 0;
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/oauth2-login")
    public ResponseEntity<?> oauth2Login(@RequestBody Map<String, String> params) {
        log.info("OAuth2로그인 시도");
        String code = params.get("code");
        String redirectUri = params.get("redirect_uri");

        String url = "https://oauth2.googleapis.com/token";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code",code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        log.info("OAuth2 로그인 시작");
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, String> tokenResponse = response.getBody();
                String accessToken = tokenResponse.get("access_token");

                // Access Token을 사용해 사용자 정보 가져오기
                String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

                HttpHeaders userHeaders = new HttpHeaders();
                userHeaders.set("authorization", "Bearer " + accessToken);

                HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
                ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userRequest, Map.class);

                if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> userInfo = userInfoResponse.getBody();
                    String UserId = (String) userInfo.get("email");
                    String name = (String) userInfo.get("name");

                    User user = userRepo.findById(UserId).orElseGet (() -> {
                        User newUser = new User(UserId, name);
                        userRepo.save(newUser);
                        log.info("oauth 로그인 유저 정보 {}",newUser);
                        log.info("oauth userID : {}", UserId);
                        log.info("oauth name : {}", name);
                        return newUser;
                    });

                    // JWT 토큰 생성
                    String jwtToken = jwtService.getToken("id", user.getUserId());

                    // 클라이언트에게 JWT 토큰 반환
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.add("authorization", jwtToken);

                    return new ResponseEntity<>("로그인 성공", responseHeaders, HttpStatus.OK);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 정보 요청 실패");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰 요청 실패");
            }
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 요청 오류" + e.getMessage());
        }
    }

    // 토큰에서 사용자 ID 가져오기
    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(@RequestHeader("authorization") String accessToken) {
        log.info("getUserInfo() 호출 : " + accessToken);
        Claims claims = jwtService.getClaims(accessToken);

        if (claims != null) {
            String userId = claims.get("id").toString();
            User user = userRepo.findById(userId).orElse(null);

            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
    }
}

