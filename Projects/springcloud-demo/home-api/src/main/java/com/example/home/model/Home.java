package com.example.home.model;

import lombok.Data;
import java.util.Date;

@Data
public class Home {
    private Long id;
    private String name;
    private String description;
    private Date createTime;
}