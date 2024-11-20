package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.Posts;
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

import java.io.IOException;
import java.util.List;

@Service
public class PostSvc {
    PostsRepo postsRepo;
    @Autowired
    public PostSvc(PostsRepo postsRepo){
        this.postsRepo = postsRepo;
    }
    public String SavePost(Posts posts,MultipartFile file) {
        try {
            String newFile = FileUtil.SaveMedia(file);
            String originalFilename =file.getOriginalFilename();
            String splitFile = "";
            if(originalFilename != null && originalFilename.contains(".")){
                splitFile = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            }
            else{
                return "지원핮지않는 형식이다";
            }
            if( splitFile.equals("jpg") || splitFile.equals("png")){
                posts.setImage_url(newFile); //이미지일때
            }
            else {
                posts.setMedia_url(newFile); //동영상일때
            }
            postsRepo.save(posts);
            return "게시물 저장 성공";
        } catch (Exception e) {
            return "게시물 저장 실패"+e.getMessage(); // 에러메세지 console에서 보인다
        }
    }
    public Page<Posts> ShowMain(String userId,int pageNumber){

        try {
            PageRequest pageRequest = PageRequest.of(pageNumber,5,Sort.by("post_time"));
            Page<Posts> userPost = postsRepo.findByUserId(userId,pageRequest);
            if(userPost.stream().count() != 0){ //count 개수 -> 페이지 안에 들어있는 post개수
                return userPost;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
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
    public String deletePost(int Post_pk) { //repo에서 찾아서 삭제
        try {
            postsRepo.deleteById(Post_pk);
            //pk지우기 시도했을때 cascade로 해당 로우 지우기 및 관계 로우 삭제
            return "수정 성공";
        } catch (Exception e) {
            return "수정 실패";
        }
    }

    public List<Posts> ShowProfileContents(String userId){
        try {
            List<Posts> userPost = postsRepo.findContentsByUserId(userId);
            if(userPost.stream().count() != 0){ //count 개수 -> 페이지 안에 들어있는 post개수
                return userPost;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
