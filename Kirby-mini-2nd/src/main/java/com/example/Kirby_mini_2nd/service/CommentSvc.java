package com.example.Kirby_mini_2nd.service;


import com.example.Kirby_mini_2nd.repository.entity.Comments;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.CommentsRepo;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import com.example.Kirby_mini_2nd.util.FileUtil;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.util.List;

@Service
public class CommentSvc {
    CommentsRepo commentsRepo;
    @Autowired
    public CommentSvc(CommentsRepo commentsRepo){
        this.commentsRepo=commentsRepo;
    }
    public String SaveComment(Comments content){
        try{
            commentsRepo.save(content);

        return "댓글 저장 성공";
        }
        catch (Exception e){
            return "댓글 저장 실패"+e.getMessage();
        }
    }
    public List<Comments> ReadComment(int postPk){
        try {
            List<Comments> comments = commentsRepo.findByPostPk(postPk);
            return comments;
        }catch(Exception e){
            return null;
        }
    }
    public String UpdateComment(int commentPk,String content){
        try{
            //pk받아서 객체가져와
            // 객체의 컨텐트만 set해
            // 저장해
            // 리턴해
            Comments updatedComment =commentsRepo.findById(commentPk).get();
            updatedComment.setContent(content);
            commentsRepo.save(updatedComment);
            return "댓글 수정 성공";
        } catch (Exception e) {
            return "댓글 수정 실패";
        }
    }
    public String DeleteComment(int commentPk){
        try{
            commentsRepo.deleteById(commentPk);
            return "댓글 삭제 성공";
        } catch (Exception e) {
            return  "댓글 삭제 실패";
        }
    }
}