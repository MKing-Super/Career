package com.example.work.model;

import lombok.Data;
import java.util.Date;

@Data
public class Work {
    private Long id;
    private String name;
    private String description;
    private Date createTime;
}