package com.example.config;

import com.example.mapper.*;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapStructConfig {
    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public RoleMapper roleMapper() {
        return Mappers.getMapper(RoleMapper.class);
    }

    @Bean
    public BrandMapper brandMapper() {
        return Mappers.getMapper(BrandMapper.class);
    }

    @Bean
    public ParentCategoryMapper parentCategoryMapper() {
        return Mappers.getMapper(ParentCategoryMapper.class);
    }

    @Bean
    public ChildCategoryMapper childCategoryMapper() {
        return Mappers.getMapper(ChildCategoryMapper.class);
    }

    @Bean
    public ProductMapper productMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }

    @Bean
    public CartMapper cartMapper() {
        return Mappers.getMapper(CartMapper.class);
    }

    @Bean
    public OrderMapper orderMapper() {
        return Mappers.getMapper(OrderMapper.class);
    }

    @Bean
    public OrderLineMapper orderLineMapper() {
        return Mappers.getMapper(OrderLineMapper.class);
    }

    @Bean
    public CommentMapper commentMapper() {
        return Mappers.getMapper(CommentMapper.class);
    }

}
