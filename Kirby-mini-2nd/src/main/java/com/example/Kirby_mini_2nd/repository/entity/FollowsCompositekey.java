package com.example.Kirby_mini_2nd.repository.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class FollowsCompositekey implements Serializable {
    private String follower_id;
    private String following_id;

    public FollowsCompositekey() {}

    public FollowsCompositekey(String id1, String id2) {
        this.follower_id = id1;
        this.following_id = id2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowsCompositekey that = (FollowsCompositekey) o;
        return Objects.equals(follower_id, that.follower_id) && Objects.equals(following_id, that.following_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower_id, following_id);
    }
}
