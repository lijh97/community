package com.example.demo.dto;

import lombok.*;

@Data
public class GithubUser {
    private String name;
    private String bio;
    private long id;
    private String avatarUrl;
}
