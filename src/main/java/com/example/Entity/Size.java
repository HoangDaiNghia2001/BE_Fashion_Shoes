package com.example.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
}
