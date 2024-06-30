package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parent_category")
public class ParentCategory extends BaseEntity{
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brandOfParentCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "parentCategoryOfChildCategory", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<ChildCategory> childCategories = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "parentCategoryOfProduct", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Brand getBrand() {
        return brandOfParentCategory;
    }

    public void setBrand(Brand brandOfParentCategory) {
        this.brandOfParentCategory = brandOfParentCategory;
    }

    public Set<ChildCategory> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(Set<ChildCategory> childCategories) {
        this.childCategories = childCategories;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public ParentCategory() {
    }

    public ParentCategory(String name, Brand brandOfParentCategory, Set<ChildCategory> childCategories, Set<Product> products) {
        this.name = name;
        this.brandOfParentCategory = brandOfParentCategory;
        this.childCategories = childCategories;
        this.products = products;
    }
}
