package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User();
        user.setId(1L);
        user.setEmail("ivanIvanov@gmail.com");
        user.setLogin("ivan1212");
        user.setName("ivan");
        user.setBirthday(LocalDate.of(1985,5,5));
    }

    @Test
    void shouldReturnUsers() {
        userController.createUser(user);
        Collection<User> users = userController.getUsers();
        assertEquals(1, users.size());
    }

    @Test
    void shouldCreateUser() {
        User added = userController.createUser(user);
        assertNotNull(added.getId());
        assertEquals("ivan", added.getName());
    }

    @Test
    void shouldUpdateUser() {
        User added = userController.createUser(user);
        added.setName("vladimir");
        User updated = userController.updateUser(added);
        assertEquals("vladimir", updated.getName());
        assertEquals(added.getId(), updated.getId());
    }

    @Test
    void shouldSetNameToLoginIfNameIsNull() {
        user.setName(null);
        User added = userController.createUser(user);
        assertEquals("ivan1212", added.getName());
    }

    @Test
    void shouldSetNameToLoginIfNameIsBlank() {
        user.setName(" ");
        User added = userController.createUser(user);
        assertEquals("ivan1212", added.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        user.setId(1111L);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.updateUser(user));
        assertEquals("Пользователь с id [1111] не найден", ex.getMessage());
    }

}