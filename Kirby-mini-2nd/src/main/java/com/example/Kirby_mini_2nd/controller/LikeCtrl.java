package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Likes;
import com.example.Kirby_mini_2nd.repository.repo.LikesRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LikeCtrl {
    LikesRepo likesRepo;
    public LikeCtrl(Likes likes) {
        this.likesRepo = likesRepo;
    }
    //@GetMapping("/like")
    //public ResponseEntity<ResponseModel>readLike(@)



}
