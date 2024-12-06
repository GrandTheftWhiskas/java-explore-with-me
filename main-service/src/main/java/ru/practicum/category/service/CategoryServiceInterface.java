package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryServiceInterface {
    CategoryDto add(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto, Long catId);

    CategoryDto get(Long catId);

    List<CategoryDto> getAll(int from, int size);

    void delete(Long catId);
}
