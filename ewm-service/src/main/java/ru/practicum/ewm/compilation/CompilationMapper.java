package ru.practicum.ewm.compilation;

import org.mapstruct.AnnotateWith;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;

import java.util.Collection;
import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = {EventMapper.class})
@AnnotateWith(value = Component.class)
public interface CompilationMapper {
    CompilationDto map(Compilation obj);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation map(NewCompilationDto obj, Collection<Event> events);

    List<CompilationDto> map(List<Compilation> compilations);
}
