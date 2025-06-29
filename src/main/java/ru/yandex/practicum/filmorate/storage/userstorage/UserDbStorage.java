package ru.yandex.practicum.filmorate.storage.userstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    @Autowired
    protected JdbcTemplate jdbc;

    private static final String FIND_BY_ID_QUERY =
            """
            SELECT *
            FROM users
            WHERE id = ?
            """;

    private static final String FIND_ALL_QUERY =
            """
            SELECT *
            FROM users
            """;

    private static final String INSERT_QUERY =
            """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_QUERY =
            """
            UPDATE users SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;

    private static final String EXISTS_QUERY =
            """
            SELECT EXISTS(
                SELECT 1
                FROM users
                WHERE id = ?
                )
            """;

    @Override
    public boolean existsById(Long userId) {
        Boolean exists = jdbc.queryForObject(EXISTS_QUERY, Boolean.class, userId);
        return exists != null && exists;
    }

    @Override
    public Optional<User> getUserById(Long userId) throws ValidationException {
        try {
            User result = jdbc.queryForObject(FIND_BY_ID_QUERY, new UserRowMapper(), userId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getUsers() {
        log.info("Получение списка пользователей");
        Collection result = jdbc.query(FIND_ALL_QUERY, new UserRowMapper());
        return result;
    }

    @Override
    public User createUser(User newUser) {
        log.info("Создание нового пользователя [{}]", newUser);
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            log.warn("У пользователя с логином [{}] отсутствует имя. Имя присвоено в соответствии с логином!", newUser.getLogin());
            newUser.setName(newUser.getLogin());
        }
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
                ps.setString(1, newUser.getEmail());
                ps.setString(2, newUser.getLogin());
                ps.setString(3, newUser.getName());
                ps.setDate(4, Date.valueOf(newUser.getBirthday()));
                return ps;
            }, keyHolder);
            Long userId = keyHolder.getKey().longValue();
            newUser.setId(userId);
            return newUser;
        } catch (InternalServerException ex) {
            throw new RuntimeException("Не удалось создать пользователя!");
        }
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление пользователя с id [{}]", user.getId());
        if (user.getId() == null) {
            throw new ValidationException("Для обновления пользователя необходимо указать id!");
        }
        Optional<User> mayBeUser = getUserById(user.getId());
        if (mayBeUser.isPresent()) {
            User oldUser = mayBeUser.get();
            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
            }
            if (user.getLogin() != null) {
                oldUser.setLogin(user.getLogin());
            }
            if (user.getName() != null) {
                if (!user.getName().isBlank()) {
                    oldUser.setName(user.getName());
                }
            }
            if (user.getBirthday() != null) {
                oldUser.setBirthday(user.getBirthday());
            }

            int rowsUpdated = jdbc.update(UPDATE_QUERY, oldUser.getEmail(), oldUser.getLogin(), oldUser.getName(),
                    oldUser.getBirthday(), oldUser.getId());
            log.info("Пользователь с id [{}] обновлен", user.getId());
            log.debug("Пользователь [{}]", user);
            if (rowsUpdated == 0) {
                throw new InternalServerException("Не удалось обновить данные!");
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден!");
    }

}