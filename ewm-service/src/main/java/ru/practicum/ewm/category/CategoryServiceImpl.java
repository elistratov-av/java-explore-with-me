package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto newCategory) {
        Category category = categoryRepository.save(categoryMapper.map(newCategory));
        return categoryMapper.map(category);
    }

    @Override
    @Transactional
    public void delete(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto newCategory) {
        // проверяем необходимые условия
        Category oldCategory = categoryRepository.findById(newCategory.getId())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + newCategory.getId() + " не найдена"));

        // если категория найдена и все условия соблюдены, обновляем ее содержимое
        if (StringUtils.isNoneBlank(newCategory.getName()))
            oldCategory.setName(newCategory.getName());

        Category category = categoryRepository.save(oldCategory);
        return categoryMapper.map(category);
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categoryMapper.map(categories.getContent());
    }

    @Override
    public CategoryDto findById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + " не найдена"));
        return categoryMapper.map(category);
    }
}
