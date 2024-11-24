package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Likes;
import com.example.Kirby_mini_2nd.repository.repo.LikesRepo;
import com.example.Kirby_mini_2nd.service.LikeSvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LikeCtrl {
    LikeSvc likeSvc;
    public LikeCtrl(LikeSvc likeSvc) {
        this.likeSvc = likeSvc;
    }
    @GetMapping("/like/{postPk}")
    public ResponseEntity<ResponseModel>readLike(@PathVariable int PostPk){
        List<Likes> readLikes = likeSvc.searchPostLike(PostPk);
        return ResponseModel.MakeResponse(readLikes, HttpStatus.OK);
    }
    @PostMapping("/like")
    public ResponseEntity<ResponseModel>likeClick(@RequestParam int PostPk,@RequestParam String userId){
        String saveLikes= likeSvc.addLike(PostPk,userId);
        return ResponseModel.MakeResponse(saveLikes,HttpStatus.OK);
    }



}
