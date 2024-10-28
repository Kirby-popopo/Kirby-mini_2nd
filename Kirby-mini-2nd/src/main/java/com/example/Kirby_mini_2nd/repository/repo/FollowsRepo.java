package com.example.Kirby_mini_2nd.repository.repo;

import com.example.Kirby_mini_2nd.repository.entity.Follows;
import com.example.Kirby_mini_2nd.repository.entity.FollowsCompositekey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FollowsRepo extends JpaRepository<Follows, FollowsCompositekey> {
    @Query("SELECT f FROM Follows f WHERE f.follower_id = :follower_id")
    public List<Follows> findByFollowerId(@Param("follower_id") String follower_id);

    @Query("SELECT f FROM Follows f WHERE f.following_id = :following_id")
    public List<Follows> findByFollowingId(@Param("following_id") String following_id);
}
