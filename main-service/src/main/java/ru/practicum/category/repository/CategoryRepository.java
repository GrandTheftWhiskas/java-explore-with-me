package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category save(Category category);

    Category findCategoryById(Long catId);

    @Query(value = "SELECT * FROM categories " +
    "LIMIT ?1", nativeQuery = true)
    List<Category> findAll(int size);

    void deleteCategoryById(Long catId);
}
