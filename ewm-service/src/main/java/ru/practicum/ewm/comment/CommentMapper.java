package ru.practicum.ewm.comment;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;

import java.util.List;

@Mapper(uses = {EventMapper.class, UserMapper.class})
@AnnotateWith(value = Component.class)
public interface CommentMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "author", source = "author.id")
    CommentDto map(Comment obj);

    List<CommentDto> map(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    Comment map(NewCommentDto newComment, Event event, User author);
}
