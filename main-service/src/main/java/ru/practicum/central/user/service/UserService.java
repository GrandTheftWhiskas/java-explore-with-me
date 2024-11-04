package ru.practicum.central.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.central.user.dto.UserDto;
import ru.practicum.central.user.mapper.UserMapper;
import ru.practicum.central.user.model.User;
import ru.practicum.central.user.repository.UserRepository;

import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userDbRepository;

    @Transactional
    public UserDto post(UserDto userDto) {
        log.info("Добавление пользователя");
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        if (userDbRepository.getEmail().contains(user.getEmail())) {
            throw new IllegalArgumentException("Указанный email уже существует");
        }

        return UserMapper.toUserDto(userDbRepository.save(user));
    }

    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Обновление пользователя");
        User user = userDbRepository.getUserById(userId);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        if (!Objects.equals(userDbRepository.getUserById(userId).getEmail(), user.getEmail())) {
            if (userDbRepository.getEmail().contains(user.getEmail())) {
                throw new IllegalArgumentException("Указанный email уже существует");
            }
        }
        user.setId(userId);
        return UserMapper.toUserDto(userDbRepository.save(user));
    }


    public UserDto get(Long userId) {
        log.info("Получение пользователя");
        return UserMapper.toUserDto(userDbRepository.getUserById(userId));
    }


    public void delete(Long userId) {
        log.info("Получение пользователей");
        userDbRepository.deleteById(userId);
    }
}
