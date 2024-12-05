package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        log.info("Получение категории");
        return categoryService.get(catId);
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(required = false, defaultValue = "0") int from,
                                    @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Получение категорий");
        return categoryService.getAll(from, size);
    }
}
