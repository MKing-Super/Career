package com.example.life.model;

import lombok.Data;

import java.util.Date;

@Data
public class Life {
    private Long id;
    private String name;
    private String description;
    private Date createTime;
}
