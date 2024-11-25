package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(uniqueConstraints ={@UniqueConstraint(columnNames = {"post_id","user_id"})})
//복합유니크키 : 중복방지

public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likes_pk;

    @ManyToOne
    @JoinColumn(name = "post_pk",nullable = false)
    private Posts post_pk;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user_id;
    // 종민이 형이 알려줬던거 join쿼리안쓰고 관계 데이터 가져오도록 설정하기

    public Likes(){}

    public Likes(Posts pk,User id){
        this.post_pk=pk;
        this.user_id=id;
    }
}