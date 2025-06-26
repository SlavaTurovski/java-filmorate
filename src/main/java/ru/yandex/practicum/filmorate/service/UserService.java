package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserDbStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userStorage;
    private final FriendDbStorage friendStorage;

    public Collection<User> getUsers() {
        log.info("Поиск всех пользователей");
        return userStorage.getUsers();
    }

    public User getUserById(Long userId) {
        log.info("Поиск пользователя по id");
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден!"));
    }

    public User createUser(User newUser) {
        log.info("Создание пользователя");
        return userStorage.createUser(newUser);
    }

    public User updateUser(User updatedUser) {
        log.info("Обновление данных пользователя");
        return userStorage.updateUser(updatedUser);
    }

    public void addFriend(Long id, Long friendId) {
        log.info("Добавление в друзья");
        Optional<User> mayBeUser = userStorage.getUserById(id);
        Optional<User> mayBeFriend = userStorage.getUserById(friendId);
        if (mayBeUser.isEmpty() || mayBeFriend.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        friendStorage.addFriends(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        log.info("Удаление из друзей");
        Optional<User> mayBeUser = userStorage.getUserById(id);
        Optional<User> mayBeFriend = userStorage.getUserById(friendId);
        if (mayBeUser.isEmpty() || mayBeFriend.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        friendStorage.removeFriends(id, friendId);
    }

    public Collection<User> getMutualFriendsList(Long id, Long otherId) {
        log.info("Получение списка общих друзей");
        Optional<User> mayBeUser = userStorage.getUserById(id);
        Optional<User> mayBeOtherUser = userStorage.getUserById(otherId);
        if (mayBeUser.isEmpty() || mayBeOtherUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        return friendStorage.getCommonFriends(id, otherId);
    }

    public Collection<User> getFriends(Long id) {
        log.info("Получение списка друзей");
        Optional<User> mayBeUser = userStorage.getUserById(id);
        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }
        return friendStorage.getFriends(id);
    }

}