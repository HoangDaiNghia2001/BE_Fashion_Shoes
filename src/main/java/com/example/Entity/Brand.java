package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brand")
public class Brand extends BaseEntity {
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "brandProduct", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "brandOfParentCategory", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<ParentCategory> parentCategories = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<ParentCategory> getParentCategories() {
        return parentCategories;
    }

    public void setParentCategories(Set<ParentCategory> parentCategories) {
        this.parentCategories = parentCategories;
    }

    public Brand() {
    }

    public Brand(String name, Set<Product> products, Set<ParentCategory> parentCategories) {
        this.name = name;
        this.products = products;
        this.parentCategories = parentCategories;
    }
}
