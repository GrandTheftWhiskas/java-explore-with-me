package ru.practicum.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category save(Category category);

    Category findCategoryById(Long catId);


    Page<Category> findAll(Pageable pageable);

    void deleteCategoryById(Long catId);
}