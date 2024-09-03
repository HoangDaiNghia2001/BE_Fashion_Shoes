package com.example.mapper;

import com.example.Entity.Role;
import com.example.request.RoleRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // các field có giá trị NULL trong request sẽ không được map
    void roleRequestToRole(RoleRequest roleRequest, @MappingTarget Role role);
}
