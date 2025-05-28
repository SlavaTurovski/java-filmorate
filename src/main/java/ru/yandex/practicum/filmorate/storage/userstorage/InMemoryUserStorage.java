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

    @Override
    public User updateUser(User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ValidationException("Для обновления пользователя необходимо указать id");
        }
        if (users.containsKey(updatedUser.getId())) {
            users.put(updatedUser.getId(), updatedUser);
            return updatedUser;
        }
        throw new NotFoundException(String.format("Пользователь с id = %d  - не найден", updatedUser.getId()));
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