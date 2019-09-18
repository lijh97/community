package com.example.demo.dto;

import lombok.Data;
import model.Question;
import model.User;

@Data
public class QuestionDTO {
    private Question question;
    private User user;
}
