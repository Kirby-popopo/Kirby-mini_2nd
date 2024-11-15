package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepo extends JpaRepository <Posts, Integer> { //post 의 모든 컬럼이니까 Posts타입에 p select
    @Query("SELECT p FROM Posts p WHERE p.user_id = :userId")
    public List<Posts> findByUserId(@Param("userId")String id);


} // table type , PK type

