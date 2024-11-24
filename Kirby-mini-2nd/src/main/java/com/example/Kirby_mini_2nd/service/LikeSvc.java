package com.example.Kirby_mini_2nd.service;

import com.example.Kirby_mini_2nd.repository.entity.Likes;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.entity.User;
import com.example.Kirby_mini_2nd.repository.repo.LikesRepo;
import com.example.Kirby_mini_2nd.repository.repo.PostsRepo;
import com.example.Kirby_mini_2nd.repository.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Integer.parseInt;

@Service

public class LikeSvc {
    @Autowired
    private LikesRepo likesRepo;
    private UserRepo userRepo;
    private PostsRepo postsRepo;

    //postPk로 좋아요 보여줌
    public List<Likes> searchPostLike(int postPk) {
        try {
            List<Likes> allLikes = likesRepo.findByPostPk(postPk);
            return allLikes;
        } catch (Exception e) {
            return null;
        }
    }
    //like누적시키고 누적값 반환할거임
    // 회원 중복확인도 해야함
    public String addLike(int postPk, String userId) {
        try {
            //postPk랑 userId랑 존재하면 해당row 삭제
            // postPk와 userId를 동시에 넣어준다.
            // if (return type boolean)
            // 연산값 혹은 내부에 들어가는 메서드의 리턴 타입이 boolean 이어야만 한다.
            Posts pk=postsRepo.findById(postPk).get();
            User id=userRepo.findById(userId).get();
            Likes uniqueLike = likesRepo.findByUniqueKey(pk,id);
            if ( uniqueLike == null) {
                //new 하면 메모리 새로할당해준다
                //new Likes() 하면 Likes크키만큼 할당 이게바로 생성자
                //new Likes() 내부에 아무것도 안넣으면 빈값 혹은 사전에 디폴트 값들이 채워져.
                //new Likes() 생성자를 따로 정의해 두면 내부에 값을 미리 넣어서 생성이 가능해.
                Likes jioni = new Likes(pk,id);
                //findById optional type나온다
                //findById 하면 Pk값으로 해당 row 찾는것 즉 type은 그 엔터티 일것
                //jioni.setPost_pk(pk);
                //jioni.setUser_id(id);
                //생성자가 set역할 해주고있다
                likesRepo.save(jioni);
                return "좋아요 추가";
            }
            else {
                likesRepo.deleteById(uniqueLike.getLikes_pk());// 저장된
                return "좋아요 삭제";
            }

        } catch (Exception e) {
            return null;
        }
    }
    public Boolean checkLike(int postPk,String userId){
        try {
            User id=userRepo.findById(userId).get();
            Posts pk=postsRepo.findById(postPk).get();
            if (likesRepo.findByUniqueKey(pk,id) !=null){
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
