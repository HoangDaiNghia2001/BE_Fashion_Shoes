package com.example.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Size {
    @Column(name= "id")
    private String id;

    @Column(name = "name")
    private int name;

    @Column(name = "quantity")
    private int quantity;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Size() {
    }

    public Size(String id, int name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return Objects.equals(name, size.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
