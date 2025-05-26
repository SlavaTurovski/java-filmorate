package ru.yandex.practicum.filmorate.storage.userstorage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    User createUser(User newUser);

    User updateUser(User updatedUser);

    Optional<User> getUserById(Long id);

}