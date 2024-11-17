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
        // 현재 해당 유저의 모든 게시글을 가져오는 코드임
        // 변경해야 하는 사항.
        // showMain()에서 파라미터로 해당 유저의 id받고, 첫 요청시 0페이지 가져오기 // 해당유저의 게시물 가져올거임
        // 다음 스크롤마다 0페이지 -> 1페이지 등 1씩 늘어나는 요청을 해야함.
        // findby로 찾아오는 애를 리스트가아닌 page로 만들어서 page로 받아옴.
        // 페이징 처리 하는 부분을 넣어줘서 0, 1 동적으로 받아서 처리하면 됨.
        // 만약 페이징 처리하는 부분에서 마지막 페이지, 즉 해당 유저의 게시글이 전부 보여지면
        // 그 이후 요청은 다른 방식으로 다 보여줬다를 표시하게 리턴해주면됨.
        try {
            List userPost = postsRepo.findByUserId(userId);
            userPost.equals(postsRepo.findByUserId(userId));

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
