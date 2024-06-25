package com.example.service;

import com.example.Entity.Role;
import com.example.exception.CustomException;
import com.example.request.RoleRequest;

import java.util.List;

public interface RoleService {
    Role createRole(RoleRequest role) throws CustomException;
    String deleteRole(Long id) throws CustomException;
    Role updateRole(Long id, RoleRequest role) throws CustomException;
    Role findByName(String name) throws CustomException;

    List <Role> getAllRole();
}
