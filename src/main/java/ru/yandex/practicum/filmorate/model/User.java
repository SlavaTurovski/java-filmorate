package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Data
public class User {

    private Long id;

    @Email
    private String email;

    @NotBlank
    private String login;
    private String name;

    @Past
    private LocalDate birthday;

}