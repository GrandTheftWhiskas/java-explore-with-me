package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
    @NotBlank
    @Length(min = 6, max = 254)
    @Email
    private String email;
}
