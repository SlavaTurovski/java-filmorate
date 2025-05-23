package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Создание нового пользователя [{}]", newUser);
        if (newUser.getLogin().contains(" ")) {
            log.warn("Логин [{}] содержит пробелы. Пользователь не создан", newUser.getLogin());
            throw new ValidationException("Логин содержит пробелы");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("У пользователя с логином [{}] отсутствует имя. Имя присвоено в соответствии с логином", newUser.getLogin());
        }

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь c логином [{}] успешно создан, присвоен id [{}]", newUser.getLogin(), newUser.getId());
        log.debug("Пользователь [{}]", newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Обновление пользователя с id [{}]", updatedUser);
        if (!users.containsKey(updatedUser.getId())) {
            log.warn("Пользователь с id [{}] не найден", updatedUser.getId());
            throw new ValidationException("Пользователь с id [" + updatedUser.getId() + "] не найден");
        }

        if (updatedUser.getLogin().contains(" ")) {
            log.info("Логин [{}] содержит пробелы. Пользователь не обновлён", updatedUser.getLogin());
            throw new ValidationException("Логин содержит пробелы");
        }

        if (updatedUser.getName().isBlank()) {
            updatedUser.setName(updatedUser.getLogin());
            log.info("У пользователя с логином [{}] отсутствует имя. Логин будет использован как имя", updatedUser.getLogin());
        }

        users.put(updatedUser.getId(), updatedUser);
        log.info("Пользователь с id [{}] обновлён", updatedUser.getId());
        log.debug("Пользователь [{}]", updatedUser);
        return updatedUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}