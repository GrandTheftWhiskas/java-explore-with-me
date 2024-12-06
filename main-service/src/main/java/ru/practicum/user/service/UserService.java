package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.model.User;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;

    @Transactional
    public UserDto add(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public List<UserDto> get(List<Integer> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null) {
            return userRepository.findAll(pageable).stream()
                    .map(user -> UserMapper.toUserDto(user)).toList();
        }

        ids = ids.stream().sorted().toList();
        return userRepository.findByIdIn(ids, pageable).stream()
                .map(user -> UserMapper.toUserDto(user)).toList();
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
