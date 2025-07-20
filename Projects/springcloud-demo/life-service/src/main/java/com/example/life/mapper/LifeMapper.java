package com.example.life.mapper;

import com.example.life.model.Life;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LifeMapper {
    @Select("SELECT * FROM work")
    List<Life> findAll();

    @Insert("INSERT INTO work(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Life work);
}