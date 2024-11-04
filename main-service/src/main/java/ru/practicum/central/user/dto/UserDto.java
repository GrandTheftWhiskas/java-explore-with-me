package ru.practicum.central.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.central.user.Create;
import ru.practicum.central.user.Update;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    private String email;
}

