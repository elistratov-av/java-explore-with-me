package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserDto> findAll(Long[] ids, Pageable pageable);

    UserDto add(NewUserRequest newUser);

    void delete(long userId);
}
