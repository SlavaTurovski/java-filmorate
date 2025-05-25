package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        log.info("Обновление пользователя [{}]", updatedUser);
        return userService.updateUser(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление пользователю с id = [{}] друга с id = [{}]", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Удаление у пользователя с id = [{}] друга с id = [{}]", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriendsList(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получение списка общих друзей пользователя с id = [{}] и пользователя с id = [{}]", id, otherId);
        return userService.getMutualFriendsList(id, otherId);
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        log.info("Получение пользователя с id = [{}]", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получение списка друзей пользователя с id = [{}]", id);
        return userService.getFriends(id);
    }


}