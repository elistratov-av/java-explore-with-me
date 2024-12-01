package ru.practicum.ewm.event;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;

import java.util.List;

@Mapper(uses = {CategoryMapper.class, UserMapper.class})
@AnnotateWith(value = Component.class)
public interface EventMapper {
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", source = "created")
    @Mapping(target = "publishedOn", source = "published")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "countComments", ignore = true)
    EventFullDto map(Event obj);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "requests", ignore = true)
    Event map(NewEventDto obj, Category category, User initiator);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "countComments", ignore = true)
    EventShortDto mapShort(Event obj);

    List<EventShortDto> mapShort(Iterable<Event> events);

    List<EventFullDto> mapFull(Iterable<Event> events);
}
