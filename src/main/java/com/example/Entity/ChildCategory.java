package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "child_category")
public class ChildCategory extends BaseEntity{
    private String name;

//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parentCategory_id")
    private ParentCategory parentCategoryOfChildCategory;

    @JsonIgnore
    @OneToMany(mappedBy = "childCategoryOfProduct", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public ParentCategory getParentCategory() {
        return parentCategoryOfChildCategory;
    }

    public void setParentCategory(ParentCategory parentCategoryOfChildCategory) {
        this.parentCategoryOfChildCategory = parentCategoryOfChildCategory;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public ChildCategory() {
    }

    public ChildCategory(String name, ParentCategory parentCategoryOfChildCategory, Set<Product> products) {
        this.name = name;
        this.parentCategoryOfChildCategory = parentCategoryOfChildCategory;
        this.products = products;
    }
}
