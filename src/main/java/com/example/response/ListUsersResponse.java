package com.example.response;

import com.example.Entity.User;

import java.util.List;

public class ListUsersResponse {
    private List<UserResponse> users;
    private long total;

    public ListUsersResponse() {
    }

    public ListUsersResponse(List<UserResponse> users, long total) {
        this.users = users;
        this.total = total;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
