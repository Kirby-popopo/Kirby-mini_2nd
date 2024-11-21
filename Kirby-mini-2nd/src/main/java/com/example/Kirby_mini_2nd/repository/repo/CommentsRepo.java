package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentsRepo extends JpaRepository<Comments,Integer> {
    @Query("Select c from Comments c where c.post_pk=:post_pk")
    public List<Comments> findByPostPk(@Param("post_pk")int pk);// 아직 수정필요 type모르겠음
    //엔티티 하나받는다 Comments  ; 엔티티 하나
    // 하지만 댓글 여러개니까 나는 리스트로 받을래
    
}
