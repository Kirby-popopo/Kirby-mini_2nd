package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import com.example.Kirby_mini_2nd.service.PostSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*") //도메인 요청 전부 권한허용
public class PostCtrl {
    PostSvc postSvc;

    @Autowired
    public PostCtrl(PostSvc postSvc){
        this.postSvc=postSvc;
    }
    @PostMapping("/post")//매핑명 변경예정
    public ResponseEntity<ResponseModel> SavePost(@RequestBody Map<String, String>requestData){
        //Posts post = requestData.get("postsDetail");
        Posts post = new Posts();
        String result = postSvc.SavePost(post);
        return ResponseModel.MakeResponse(result, HttpStatus.OK);
    }

}
//객체 만들어서 서비스에 보낼거임


