package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import com.example.Kirby_mini_2nd.service.PostSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") //도메인 요청 전부 권한허용
public class PostCtrl {
    PostSvc postSvc;

    @Autowired
    public PostCtrl(PostSvc postSvc){
        this.postSvc=postSvc;
    }
    @PostMapping("/post")// 매핑명 변경예정
    public ResponseEntity<ResponseModel> SavePost(@RequestBody Map<String, Posts>requestData){
        Posts post = requestData.get("postsDetail");
        //Posts post = new Posts();
        String result = postSvc.SavePost(post);
        return ResponseModel.MakeResponse(result, HttpStatus.OK);
    }
    @PostMapping("/mainPage")
    public ResponseEntity<ResponseModel> ShowMain(@RequestBody Map<String,String>requestData){
        String userId =requestData.get("userId");
        int page = Integer.parseInt(requestData.get("page"));
        Posts post =new Posts();
        Page<Posts> main = postSvc.ShowMain(userId,page);

        return ResponseModel.MakeResponse(main,HttpStatus.OK);
    }
    @PutMapping("/post")
    public ResponseEntity<ResponseModel> updatePost(@RequestBody Map<String,Posts>requestData){
        Posts post = requestData.get("postsDetail");
        String updated = postSvc.UpdatePost(post);
        return ResponseModel.MakeResponse(updated,HttpStatus.OK);
    }
    @PostMapping("/postDelete")
    public ResponseEntity<ResponseModel> deletePost(@RequestBody Map<String,Integer>requestData){
        int post = requestData.get("post_pk");
        String deleted = postSvc.deletePost(post);
        return ResponseModel.MakeResponse(deleted,HttpStatus.OK);
    }
}
//객체 만들어서 서비스에 보낼거임


