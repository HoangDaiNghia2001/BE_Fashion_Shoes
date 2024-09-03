package com.example.mapper;

import com.example.Entity.User;
import com.example.request.UserRequest;
import com.example.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Map struct dựa vào các hàm get/set của DTO và Entity để map với nhau
    // Không thể sử dụng Map struct nếu không có các hàm get/set
    @Mapping(source = "avatarBase64", target = "imageBase64")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "roles", ignore = true)
    UserResponse userToUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // các field có giá trị NULL trong request sẽ không được map
    void userRequestToUser(UserRequest userRequest, @MappingTarget User user);
}
