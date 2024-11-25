package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Likes;
import com.example.Kirby_mini_2nd.repository.repo.LikesRepo;
import com.example.Kirby_mini_2nd.service.LikeSvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LikeCtrl {
    LikeSvc likeSvc;
    public LikeCtrl(LikeSvc likeSvc) {
        this.likeSvc = likeSvc;
    }

    @PostMapping("/like")
    public ResponseEntity<ResponseModel>likeClick(@RequestBody Map<String, String> requestData){
        String userId = requestData.get("user_id");
        int postPk = Integer.parseInt(requestData.get("post_pk"));
        String saveLikes= likeSvc.addLike(postPk,userId);
        return ResponseModel.MakeResponse(saveLikes,HttpStatus.OK);
    }

    @GetMapping("/like/{postPk}")
    public ResponseEntity<ResponseModel>readLike(@PathVariable int postPk){
        List<Likes> readLikes = likeSvc.searchPostLike(postPk);
        return ResponseModel.MakeResponse(readLikes, HttpStatus.OK);
    }

    // 유저아이디와 게시글 pk를 주면
    // 해당 유저 가 해당 게시글을 좋아요 눌렀는지 안눌렀는지 판단해주는 컨트롤러.
    @GetMapping("checkLike/{postPk}/{userId}")
    public ResponseEntity<ResponseModel>cheking(@PathVariable int postPk,@PathVariable String userId){
        Boolean Checked = likeSvc.checkLike(postPk,userId);
        return ResponseModel.MakeResponse(Checked,HttpStatus.OK);
    }
}
