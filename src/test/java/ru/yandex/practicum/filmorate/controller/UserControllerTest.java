/*
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.userstorage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new UserDbStorage()));

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("ivanIvanov@gmail.com");
        user1.setLogin("ivan1212");
        user1.setName("Ivan Sergeevich");
        user1.setBirthday(LocalDate.of(1985,5,5));

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("nastya@gmail.com");
        user2.setLogin("Anastasiya");
        user2.setName("Anastasiya Kulagina");
        user2.setBirthday(LocalDate.of(1999,12,12));

        user3 = new User();
        user3.setId(3L);
        user3.setEmail("victor@gmail.com");
        user3.setLogin("Victor");
        user3.setName("Victor Tokarev");
        user3.setBirthday(LocalDate.of(1992,11,11));
    }

    @Test
    void shouldReturnUsers() {
        userController.createUser(user1);
        Collection<User> users = userController.getUsers();
        assertEquals(1, users.size());
    }

    @Test
    void shouldCreateUser() {
        User added = userController.createUser(user1);
        assertNotNull(added.getId());
        assertEquals("Ivan Sergeevich", added.getName());
    }

    @Test
    void shouldUpdateUser() {
        User added = userController.createUser(user1);
        added.setName("vladimir");
        User updated = userController.updateUser(added);
        assertEquals("vladimir", updated.getName());
        assertEquals(added.getId(), updated.getId());
    }

    @Test
    void shouldSetNameToLoginIfNameIsNull() {
        user1.setName(null);
        User added = userController.createUser(user1);
        assertEquals("ivan1212", added.getName());
    }

    @Test
    void shouldSetNameToLoginIfNameIsBlank() {
        user1.setName(" ");
        User added = userController.createUser(user1);
        assertEquals("ivan1212", added.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        user1.setId(1111L);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userController.updateUser(user1));
        assertEquals("Пользователь с id 1111 не найден", ex.getMessage());
    }

    @Test
    void shouldAddFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.addFriend(1L,2L);
        assertEquals(1,user1.getFriends().size());
        assertTrue(user1.getFriends().contains(2L));
        assertTrue(user2.getFriends().contains(1L));
    }


    @Test
    void shouldRemoveFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.addFriend(1L,2L);
        userController.removeFriend(1L, 2L);
        assertFalse(user1.getFriends().contains(2L));
        assertFalse(user1.getFriends().contains(1L));
    }

    @Test
    void shouldGetMutualFriends() {
        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.addFriend(1L,2L);
        userController.addFriend(1L,3L);
        assertEquals(user2.getFriends().contains(1L), user3.getFriends().contains(1L));
    }

}*/
