package com.example.response;

import com.example.Entity.Role;

import java.util.List;

public class ListRolesResponse {
    private List<Role> roles;
    private int total;

    public ListRolesResponse() {
    }

    public ListRolesResponse(List<Role> roles, int total) {
        this.roles = roles;
        this.total = total;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
