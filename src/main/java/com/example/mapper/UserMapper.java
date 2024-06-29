package com.example.mapper;

import com.example.Entity.User;
import com.example.request.UserRequest;
import com.example.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "avatarBase64", target = "imageBase64")
    UserResponse userToUserResponse(User user);

    void userRequestToUser(UserRequest userRequest, @MappingTarget User user);
}
