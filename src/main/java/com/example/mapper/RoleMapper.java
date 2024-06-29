package com.example.mapper;

import com.example.Entity.Role;
import com.example.request.RoleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    void roleRequestToRole(RoleRequest roleRequest, @MappingTarget Role role);
}
