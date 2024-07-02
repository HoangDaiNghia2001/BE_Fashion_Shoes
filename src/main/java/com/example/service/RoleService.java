package com.example.service;

import com.example.Entity.Role;
import com.example.request.RoleRequest;
import com.example.response.ListRolesResponse;
import com.example.response.Response;
import com.example.response.ResponseError;

import java.util.List;

public interface RoleService {
    Role createRole(RoleRequest role) throws ResponseError;

    Response deleteRole(Long id) throws ResponseError;

    Response deleteSomeRoles(List<Long> ids);

    Role updateRole(Long id, RoleRequest role) throws ResponseError;

    ListRolesResponse getAllRoles(int pageIndex, int pageSize);

    Role findByName(String name) throws ResponseError;
}
