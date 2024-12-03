package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto add(NewUserRequest newUser) {
        User user = userRepository.save(userMapper.map(newUser));
        return userMapper.map(user);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> findAll(Long[] ids, Pageable pageable) {
        Page<User> users = (ids != null && ids.length > 0) ?
                userRepository.findByIdIn(ids, pageable) : userRepository.findAll(pageable);
        return userMapper.map(users.getContent());
    }
}
