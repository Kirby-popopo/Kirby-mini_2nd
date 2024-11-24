package com.example.Kirby_mini_2nd.controller;

import com.example.Kirby_mini_2nd.model.ResponseModel;
import com.example.Kirby_mini_2nd.repository.entity.Comments;
import com.example.Kirby_mini_2nd.service.CommentSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class CommnetCtrl {
    CommentSvc commentSvc;

    @Autowired
    public CommnetCtrl(CommentSvc commentSvc){
        this.commentSvc=commentSvc;
    }

    @PostMapping("/comment")
    public ResponseEntity<ResponseModel> SaveComment(@RequestBody Comments content) {
            String savedComment = commentSvc.SaveComment(content);
            return ResponseModel.MakeResponse(savedComment, HttpStatus.OK);
    }

    @GetMapping("/comment/{postPk}")
    public ResponseEntity<ResponseModel>ReadComment(@PathVariable String postPk){
        int pk = Integer.parseInt(postPk);
        List<Comments> ListComments =commentSvc.ReadComment(pk);
        return ResponseModel.MakeResponse(ListComments,HttpStatus.OK);
    }

    @PostMapping("/updateComment")
    public ResponseEntity<ResponseModel> UpdateComment(@RequestParam("CommentPk")int commentPk,
                                                       @RequestParam ("Content")String content){
        String updated=commentSvc.UpdateComment(commentPk,content);
        return ResponseModel.MakeResponse(updated,HttpStatus.OK);
    }

    @PostMapping("/deleteComment")
    public ResponseEntity<ResponseModel> DeleteComment(@RequestParam("CommentPk")int commentPk){
        String deleted= commentSvc.DeleteComment(commentPk);
        return ResponseModel.MakeResponse(deleted,HttpStatus.OK);
    }

}
