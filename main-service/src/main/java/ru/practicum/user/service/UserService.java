package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.user.model.User;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto add(UserDto userDto) {
        System.out.println(userDto);
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> get(List<Integer> ids, int from, int size) {
        if (ids == null) {
            return userRepository.findAll(size).stream()
                    .map(user -> UserMapper.toUserDto(user)).toList();
        }

        ids = ids.stream().sorted().toList();
        return userRepository.findByIdIn(ids, size).stream()
                .map(user -> UserMapper.toUserDto(user)).toList();
    }

    public List<UserDto> getAll() {
        return userRepository.getAllUser().stream().map(user -> UserMapper.toUserDto(user)).toList();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
