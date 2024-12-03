package ru.practicum.ewm.request;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@AnnotateWith(value = Component.class)
public interface RequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto map(Request obj);

    List<ParticipationRequestDto> map(List<Request> requests);
}
