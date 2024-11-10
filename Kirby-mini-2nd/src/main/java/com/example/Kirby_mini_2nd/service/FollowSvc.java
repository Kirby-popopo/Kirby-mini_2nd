package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.repo.FollowsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowSvc {
    FollowsRepo followsRepo;

    @Autowired
    public FollowSvc(FollowsRepo followsRepo){
        this.followsRepo = followsRepo;
    }

    /**
     *  Following 으로 target 이 있는 유저들
     *  target 의 Follower 들.
     */
    public List<String> SearchFollower(String target){
        return followsRepo.findByFollowingId(target);
    }

    /**
     *  Follower 로 target 이 있는 유저들
     *  target 의 Following 목록
     */
    public List<String> SearchFollowing(String target){
        return followsRepo.findByFollowerId(target);
    }
}
