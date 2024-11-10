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

}
