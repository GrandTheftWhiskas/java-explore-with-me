package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDto add(CategoryDto categoryDto) {
        System.out.println(categoryDto);
        Category category = new Category(categoryDto.getId(), categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }


    public CategoryDto update(CategoryDto categoryDto, Long catId) {
        System.out.println(categoryDto);
        System.out.println(catId);
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(category1 -> category1.getName().equals(categoryDto.getName())).toList();
        if (!categories.isEmpty()) {
            if (!categories.get(0).getId().equals(catId)) {
                throw new ConflictException("Имя не должно повторяться");
            }
        }

        Category category = categoryRepository.findCategoryById(catId);
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public CategoryDto get(Long catId) {
        try {
            Category category = categoryRepository.findCategoryById(catId);
            return CategoryMapper.toCategoryDto(category);
        } catch (NullPointerException e) {
            throw new NotFoundException("Категория не найдена");
        }
    }

    public List<CategoryDto> getAll(int from, int size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Введены некорректные значения");
        }

        return categoryRepository.findAll(size).stream()
                .map(category -> CategoryMapper.toCategoryDto(category)).toList();
    }

    @Transactional
    public void delete(Long catId) {
        categoryRepository.deleteCategoryById(catId);
    }
}
