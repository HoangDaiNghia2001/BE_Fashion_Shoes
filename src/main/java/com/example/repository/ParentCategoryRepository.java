package com.example.repository;

import com.example.Entity.ParentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ParentCategoryRepository extends JpaRepository<ParentCategory,Long> {
    @Query("select p from ParentCategory p where p.name = ?1 and p.brandOfParentCategory.id = ?2")
    Optional<ParentCategory> findByNameAndBrandId(String name,Long brandId);

    @Query("select p from ParentCategory p where p.brandOfParentCategory.id = ?1")
    Set<ParentCategory> getAllParentCategoryByBrandId(Long brandId);

    @Query("select p from ParentCategory p where p.id=?1 and p.brandOfParentCategory.id = ?2")
    Optional<ParentCategory> findByIdAndAndBrandId(Long id, Long brandId);
}
