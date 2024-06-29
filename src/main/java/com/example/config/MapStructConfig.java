package com.example.config;

import com.example.mapper.RoleMapper;
import com.example.mapper.UserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapStructConfig {
    @Bean
    public UserMapper userMapper(){
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public RoleMapper roleMapper(){
        return Mappers.getMapper(RoleMapper.class);
    }
}
