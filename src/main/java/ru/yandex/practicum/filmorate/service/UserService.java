package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User createUser(User newUser) {
        return userStorage.createUser(newUser);
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    public void addFriend(Long id, Long friendId) {
        Optional<User> user1 = userStorage.getUserById(id);
        Optional<User> friend1 = userStorage.getUserById(friendId);

        if (user1.isEmpty() || friend1.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }

        User user = user1.get();
        User friend = friend1.get();

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void removeFriend(Long id, Long friendId) {
        Optional<User> user1 = userStorage.getUserById(id);
        Optional<User> friend1 = userStorage.getUserById(friendId);

        if (user1.isEmpty() || friend1.isEmpty()) {
            throw new NotFoundException("Пользователь не найден!");
        }

        User user = user1.get();
        User friend = friend1.get();

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public Set<User> getMutualFriendsList(Long userId, Long otherUserId) {

            Optional<User> mayBeUser = userStorage.getUserById(userId);
            Optional<User> mayBeOtherUser = userStorage.getUserById(otherUserId);

            if (mayBeUser.isEmpty() || mayBeOtherUser.isEmpty()) {
                throw new NotFoundException("Пользователь не найден!");
            }

            User user = mayBeUser.get();
            User otherUser = mayBeOtherUser.get();

        return user.getFriends().stream()
                .filter(friendId -> !friendId.equals(otherUserId))
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Set<User> getFriends(Long userId) {
        log.trace("Запущен метод поиска друзей пользователя");
        Optional<User> mayBeUser = userStorage.getUserById(userId);
        if (mayBeUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }

        User user = mayBeUser.get();

        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

}