package com.example.repository;

import com.example.Entity.ChildCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChildCategoryRepository extends JpaRepository<ChildCategory,Long> {
    @Query("select c from ChildCategory c where c.name=?1 and c.parentCategoryOfChildCategory.id = ?2")
    Optional<ChildCategory> findByNameAndParentCategoryId(String name,Long parentCategoryId);

    @Query("select c from ChildCategory c where c.parentCategoryOfChildCategory.id =?1")
    List<ChildCategory> getAllChildCategoriesByParentCategoryId(Long parentCategoryId);

    @Query("select c from ChildCategory c where c.id=?1 and c.parentCategoryOfChildCategory.id = ?2")
    Optional<ChildCategory> findByIdAndParentCategoryId(Long id, Long parentCategoryId);
}
