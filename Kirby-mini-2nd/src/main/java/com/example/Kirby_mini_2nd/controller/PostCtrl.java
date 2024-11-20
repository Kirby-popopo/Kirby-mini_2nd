package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import com.example.Kirby_mini_2nd.service.PostSvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "*") //도메인 요청 전부 권한허용
@RequestMapping("/api")
public class PostCtrl {
    PostSvc postSvc;

    @Autowired
    public PostCtrl(PostSvc postSvc){
        this.postSvc=postSvc;
    }
    @PostMapping("/post")// 매핑명 변경예정
    public ResponseEntity<ResponseModel> SavePost(@RequestParam("media")MultipartFile file,
                                                      @RequestParam("post") String postDetail){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Posts post = objectMapper.readValue(postDetail, Posts.class);

            String result = postSvc.SavePost(post,file);
            return ResponseModel.MakeResponse(result, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/mainPage")
    public ResponseEntity<ResponseModel> ShowMain(@RequestBody Map<String,String>requestData){
        String userId =requestData.get("userId");
        int page = Integer.parseInt(requestData.get("page"));
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

    @PostMapping("/profilePage")
    public ResponseEntity<ResponseModel> ShowProfileContents(@RequestBody Map<String,String>requestData){
        String userId =requestData.get("userId");
        List<Posts> profileContents = postSvc.ShowProfileContents(userId);
        return ResponseModel.MakeResponse(profileContents,HttpStatus.OK);
    }
    @PostMapping("/comment")
    public ResponseEntity<ResponseModel> getComment(@RequestBody Map<String,Integer>requestData){
        int post = requestData.get("post_pk");
        String savedComment = commentSvc.saveComment(post);
        return ResponseModel.MakeResponse(savedComment,HttpStatus.OK);
    } // postPk
    //게시물아이디 , 댓글 내용
}
//객체 만들어서 서비스에 보낼거임


