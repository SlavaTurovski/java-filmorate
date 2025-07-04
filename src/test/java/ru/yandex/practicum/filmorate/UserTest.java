package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.userstorage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class,
        UserRowMapper.class})
public class UserTest {

    private final UserDbStorage dao;
    private static User user;
    private Long i = 0L;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name("Иван" + i)
                .email("ivanich" + i + "@yandex.ru")
                .birthday(LocalDate.parse("1995-09-09"))
                .login("login")
                .build();
        i++;
    }

    @Test
    void shouldCreateUser() {
        User createdUser = dao.createUser(user);
        Collection<User> allUsers = dao.getUsers();
        assertEquals(allUsers.size(), i);
        assertThat(createdUser).hasFieldOrPropertyWithValue("id", createdUser.getId());
        assertThat(createdUser).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(createdUser).hasFieldOrPropertyWithValue("email", user.getEmail());
        assertThat(createdUser).hasFieldOrPropertyWithValue("birthday", user.getBirthday());
        assertThat(createdUser).hasFieldOrPropertyWithValue("login", user.getLogin());
    }

    @Test
    void shouldGetUserById() {
        User createdUser = dao.createUser(user);
        User foundUser = dao.getUserById(createdUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + createdUser.getId() + " не найден"));
        assertThat(foundUser).hasFieldOrPropertyWithValue("id", i);
        assertThat(foundUser).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(foundUser).hasFieldOrPropertyWithValue("email", user.getEmail());
        assertThat(foundUser).hasFieldOrPropertyWithValue("birthday", user.getBirthday());
        assertThat(foundUser).hasFieldOrPropertyWithValue("login", user.getLogin());
    }
}