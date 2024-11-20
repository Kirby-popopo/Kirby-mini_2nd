package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentsRepo extends JpaRepository<Comments,Integer> {
    @Query("Select c from Comments c where c.post_pk=:post_pk")
    public Comments findByPostPk(@Param("post_pk")String pk);// 아직 수정필요 type모르겠음
    
}
