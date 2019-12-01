package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Long questionId;
    private String outerTitle;
    private User notifier;
    private Integer status;
    private Integer type;
}
