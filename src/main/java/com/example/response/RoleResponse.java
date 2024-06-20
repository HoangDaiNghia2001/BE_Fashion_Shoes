package com.example.response;

import com.example.Entity.Role;

public class RoleResponse extends Response{
    Role role;

    public RoleResponse(Role role) {
        this.role = role;
    }

    public RoleResponse(String message, Boolean success, Role role) {
        super(message, success);
        this.role = role;
    }

    public RoleResponse() {
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
