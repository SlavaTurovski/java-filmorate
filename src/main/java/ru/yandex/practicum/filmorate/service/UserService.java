package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    private final Set<String> emails = new HashSet<>();

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

    public User createUser(User user) {
        if (!emails.add(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья");
        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователь с id = " + userId + " не может добавить самого себя в друзья");
        }
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление из друзей");
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getMutualFriendsList(Long userId, Long otherUserId) {
        log.info("Получение списка общих друзей");
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        Set<Long> userFriendsId = user.getFriends();
        return userFriendsId.stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(userStorage::getUserById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(Long userId) {
        log.info("Получение списка друзей");
        User user = getUserById(userId);
        Set<Long> userFriendsId = user.getFriends();
        return userFriendsId.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

}