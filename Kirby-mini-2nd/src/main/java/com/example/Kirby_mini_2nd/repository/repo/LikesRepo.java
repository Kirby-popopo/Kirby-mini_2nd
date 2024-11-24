package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.Likes;
import com.example.Kirby_mini_2nd.repository.entity.Posts;
import com.example.Kirby_mini_2nd.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository

public interface LikesRepo extends JpaRepository<Likes,Integer> {
    @Query("select l from Likes l where l.post_pk=:post_pk")
    List<Likes> findByPostPk(@Param("post_pk") int post_pk);

    // :user_id 처럼 쓸 때 뭐가 중요하다고 했는지 한번 생각해보기.
    @Query("select l from Likes l where l.post_pk=:post_pk and l.user_id=:user_id")
    Likes findByUniqueKey(@Param("post_pk") Posts postPk, @Param("user_id") User userId);



}