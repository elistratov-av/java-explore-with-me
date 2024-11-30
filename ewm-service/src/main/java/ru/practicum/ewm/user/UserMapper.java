package ru.practicum.ewm.user;

import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@AnnotateWith(value = Component.class)
public interface UserMapper {
    UserDto map(User obj);

    UserShortDto mapShort(User obj);

    @Mapping(target = "id", ignore = true)
    User map(NewUserRequest obj);

    List<UserDto> map(List<User> users);
}
