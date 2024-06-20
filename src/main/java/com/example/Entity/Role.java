package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
public class Role extends BaseEntity{

    @Column(name = "name")
    private String name;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Role() {
    }

    public Role(String name, String description, List<User> users) {
        this.name = name;
        this.description = description;
        this.users = users;
    }
}
