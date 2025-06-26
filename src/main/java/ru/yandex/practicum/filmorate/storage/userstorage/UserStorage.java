package ru.yandex.practicum.filmorate.storage.userstorage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    User createUser(User user) throws ValidationException;

    User updateUser(User user) throws  ValidationException;

    Optional<User> getUserById(Long userId) throws ValidationException;

}