package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.userstorage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ FriendDbStorage.class,
        UserDbStorage.class,
        UserRowMapper.class})
public class FriendTest {

    private final FriendDbStorage friendDbStorage;
    private static User user;
    private static User friend;
    private static User userBd;
    private static User friendBd;
    private Long i = 0L;

    @Autowired
    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name("Виктор" + i)
                .email("vitya" + i + "@gmail.com")
                .birthday(LocalDate.parse("1990-09-09"))
                .login("login" + i)
                .build();
        userBd = userDbStorage.createUser(user);
        i++;

        friend = User.builder()
                .name("Вика" + i)
                .email("victoriya" + i + "@yandex.ru")
                .birthday(LocalDate.parse("2000-02-02"))
                .login("login" + i)
                .build();
        friendBd = userDbStorage.createUser(friend);
        i++;
    }

    @Test
    public void shouldAddFriend() {
        Long userId = userBd.getId();
        Long friendId = friendBd.getId();
        friendDbStorage.addFriends(userId, friendId);
        Collection<User> user1Friends = friendDbStorage.getFriends(userId);
        Collection<User> user2Friends = friendDbStorage.getFriends(friendId);
        assertEquals(user1Friends.size(), 1);
        assertEquals(user2Friends.size(), 0);
    }

    @Test
    public void shouldDeleteFriend() {
        Long userId = userBd.getId();
        Long friendId = friendBd.getId();
        friendDbStorage.addFriends(userId, friendId);
        Collection<User> user1Friends = friendDbStorage.getFriends(userId);
        Collection<User> user2Friends = friendDbStorage.getFriends(friendId);
        assertEquals(user1Friends.size(), 1);
        assertEquals(user2Friends.size(), 0);
        friendDbStorage.removeFriends(userId, friendId);
        Collection<User> user1FriendsBeforeDelete = friendDbStorage.getFriends(userId);
        assertEquals(user1FriendsBeforeDelete.size(), 0);
    }

}