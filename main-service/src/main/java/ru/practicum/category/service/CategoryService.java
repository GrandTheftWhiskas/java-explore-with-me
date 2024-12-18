package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto update(CategoryDto categoryDto, Long catId) {
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

        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream()
                .map(category -> CategoryMapper.toCategoryDto(category)).toList();
    }

    @Transactional
    public void delete(Long catId) {
        if (categoryRepository.existsById(catId)) {
            categoryRepository.deleteById(catId);
        }
    }
}
