package ru.practicum.ewm.category;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@AnnotateWith(value = Component.class)
public interface CategoryMapper {
    CategoryDto map(Category obj);

    @Mapping(target = "id", ignore = true)
    Category map(NewCategoryDto obj);

    List<CategoryDto> map(List<Category> categories);
}
