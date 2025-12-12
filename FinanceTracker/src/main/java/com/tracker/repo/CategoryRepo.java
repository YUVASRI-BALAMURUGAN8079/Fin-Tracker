package com.tracker.repo;

import com.tracker.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

    @Query("SELECT category FROM Category category WHERE categoryId = :categoryId AND userId = :userId AND deletedTime = :deletedTime")
    Category findByUserIdAndCategoryCategoryId(Long categoryId, Long userId,Long deletedTime);

    @Query("SELECT category FROM Category category WHERE name = :name AND userId = :userId AND deletedTime = :deletedTime")
    Category findByUserIdAndCategoryName(String name, Long userId,Long deletedTime);

    @Query("SELECT category FROM Category category WHERE category.deletedTime = 0L AND category.userId = :userId AND deletedTime = :deletedTime")
    List<Category> findByUserIdOrderByName(Long userId,Long deletedTime);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.deletedTime = :deletedTime WHERE c.categoryId = :categoryId AND c.userId = :userId")
    void softDeleteCategory(@Param("userId") Long userId,@Param("categoryId") Long categoryId, @Param("deletedTime") Long deletedTime);

}
