package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.controller.PostCtrl;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

@Service
public class PostSvc {
    PostsRepo postsRepo;
    @Autowired
    public PostSvc(PostsRepo postsRepo){
        this.postsRepo = postsRepo;
    }

    public String SavePost(Posts posts) {
    //객체 ctrl에 있다
        //외부로 데이터가 전송될때 try catch 항상감싸주기 예를들면 repo
        try {
            postsRepo.save(posts);
            return "게시물 저장 성공";
        } catch (Exception e) {
            return "게시물 저장 실패"+e.getMessage(); // 에러메세지 console에서 보인다
        }
    }
    public List<Posts> ShowMain(String userId){
        // id가 일치하는 사람의 게시물만 보이게 할거임 vo써야하나?
        // 메인페이지 = 게시물 리스트 5개 보여주기
        // findById 하면 pk로 찾게되는데 그럼optional로 가져온다 ... getBy 써야겠지..?
        // 하나만 가져올게 아니라 모든컬럼가져와야하니까
        // 레포 쿼리문으로 다가져오기 리턴은 리스트 나오게
        try {
            return postsRepo.findByUserId(userId);
        } catch (Exception e) {
            return null;
        }
    }
    public String UpdatePost(Posts posts){ //repo에서 찾아서 수정하고 저장
        try {
            Posts updatedPost = postsRepo.findById(posts.getPost_pk()).get();
            updatedPost.setContents(posts.getContents());
            updatedPost.setLocation(posts.getLocation());
            updatedPost.setImage_url(posts.getImage_url());
            updatedPost.setMedia_url(posts.getMedia_url());
            postsRepo.save(posts);
            return "수정 성공";
        } catch (Exception e) {
            return "수정 실패";
        }
    }
    public String deletePost(Posts posts){ //repo에서 찾아서 삭제
        try {
            postsRepo.deleteById(posts.getPost_pk());
            //pk지우기 시도했을때 cascade로 해당 로우 지우기 및 관계 로우 삭제
            return "수정 성공";
        } catch (Exception e) {
            return "수정 실패";
        }
    }
}
