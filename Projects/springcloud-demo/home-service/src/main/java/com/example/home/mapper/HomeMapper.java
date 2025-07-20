package com.example.home.mapper;

import com.example.home.model.Home;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeMapper {
    @Select("SELECT * FROM work")
    List<Home> findAll();

    @Insert("INSERT INTO work(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Home work);
}