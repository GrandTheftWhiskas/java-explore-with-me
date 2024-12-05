package ru.practicum.user.mapper;

import ru.practicum.user.model.User;
import ru.practicum.user.dto.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
