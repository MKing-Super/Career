package com.example.work.mapper;

import com.example.work.model.Work;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WorkMapper {
    @Select("SELECT * FROM work")
    List<Work> findAll();

    @Insert("INSERT INTO work(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Work work);
}