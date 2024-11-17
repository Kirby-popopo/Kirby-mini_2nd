package com.example.Kirby_mini_2nd.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString

public class Hashtags {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hashtag_id;
    private String tag_name;

}
