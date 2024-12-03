package ru.practicum.ewm.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategory) {
        log.info("==> create category: {}", newCategory);
        CategoryDto category = categoryService.add(newCategory);
        log.info("<== create category: {}", category);
        return category;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        log.info("==> delete category: catId = {}", catId);
        categoryService.delete(catId);
        log.info("<== delete category");
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable long catId, @RequestBody @Valid CategoryDto newCategory) {
        log.info("==> update category: catId = {}, newCategory = {}", catId, newCategory);
        newCategory.setId(catId);
        CategoryDto category = categoryService.update(newCategory);
        log.info("<== update category: {}", category);
        return category;
    }
}
