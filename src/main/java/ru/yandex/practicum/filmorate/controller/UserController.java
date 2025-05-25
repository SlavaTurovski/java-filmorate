package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получение списка пользователей");
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Создание нового пользователя [{}]", newUser);
        return userService.createUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Обновление пользователя с id [{}]", updatedUser);
        return userService.updateUser(updatedUser);
    }

}