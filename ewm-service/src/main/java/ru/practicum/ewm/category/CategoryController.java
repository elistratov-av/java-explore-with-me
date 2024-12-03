package ru.practicum.ewm.category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.data.domain.PageRollRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("==> findAll categories: from = {}, size = {}", from, size);
        List<CategoryDto> categories = categoryService.findAll(PageRollRequest.of(from, size));
        log.info("<== findAll categories: count = {}", categories.size());
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable int catId) {
        log.info("==> findByIdIn category: catId = {}", catId);
        CategoryDto category = categoryService.findById(catId);
        log.info("<== findByIdIn category: {}", category);
        return category;
    }
}
