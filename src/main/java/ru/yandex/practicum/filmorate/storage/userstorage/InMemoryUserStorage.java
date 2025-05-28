package ru.yandex.practicum.filmorate.storage.userstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        log.info("Получение списка пользователей");
        return new ArrayList<>(users.values());
    }


    @Override
    public User createUser(User newUser) {
        log.info("Создание нового пользователя [{}]", newUser);
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь c логином [{}] успешно создан, присвоен id [{}]", newUser.getLogin(), newUser.getId());
        log.debug("Пользователь [{}]", newUser);
        return newUser;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.info("Обновление пользователя с id [{}]", updatedUser.getId());
        if (updatedUser.getId() == null) {
            throw new ValidationException("Для обновления пользователя необходимо указать id");
        }
        if (users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
            log.info("Пользователь с id [{}] обновлен", updatedUser.getId());
            log.debug("Пользователь [{}]", updatedUser);
            return updatedUser;
        }
        throw new NotFoundException("Пользователь с id " + updatedUser.getId() + " не найден");
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
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