package com.example.service;

import com.example.Entity.Role;
import com.example.request.RoleRequest;
import com.example.response.ListRolesResponse;
import com.example.response.Response;
import com.example.exception.CustomException;

import java.util.List;

public interface RoleService {
    Role getById(Long id) throws CustomException;
    Role createRole(RoleRequest role) throws CustomException;

    Response deleteRole(Long id) throws CustomException;

    Response deleteSomeRoles(List<Long> ids);

    Role updateRole(Long id, RoleRequest role) throws CustomException;

    ListRolesResponse getAllRoles();

    Role findByName(String name) throws CustomException;
}
