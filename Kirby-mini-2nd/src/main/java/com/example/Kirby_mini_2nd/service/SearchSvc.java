package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.model.dto.SearchUserDTO;
import com.example.Kirby_mini_2nd.repository.repo.FollowsRepo;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchSvc {
    UserRepo userRepo;
    /*@Autowired
    PostRepo postRepo;*/

    @Autowired
    public SearchSvc(FollowsRepo followsRepo, UserRepo userRepo){
        this.userRepo = userRepo;
    }

    /**
     *  검색하는 id로 시작하는 유저 찾기
     */
    public List<SearchUserDTO> SearchUser(String userId){
        return userRepo.findByUserIdLikeTargetId(userId);
    }



    //>-< 지오니가 구현예정
    // 게시물검색
    // 해시태그로 문자열 검색해서 문자열 포함된 해시태그PK 찾기
    // 모든게시물찾고 게시물 이미지(미리보기) 보여주기
    // #검색어
    //
    // #송지원
    // #송지원여행
    // #송지원맛집
    // 검색한 해시태그만 나옴
    // 여기에서
    //public String searchPost(String )

}
