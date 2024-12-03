package ru.practicum.ewm.category;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategory);

    void delete(long catId);

    CategoryDto update(CategoryDto newCategory);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(long catId);
}
