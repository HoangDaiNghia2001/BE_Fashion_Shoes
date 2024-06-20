package com.example.Entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {
    @PrePersist
    protected void onCreated(){
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updateAt =LocalDate.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "update_at")
    private LocalDate updateAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "update_by")
    private String updateBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDate updateAt) {
        this.updateAt = updateAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}
