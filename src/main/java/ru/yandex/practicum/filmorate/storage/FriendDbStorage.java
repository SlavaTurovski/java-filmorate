package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendDbStorage {

    private final JdbcTemplate jdbc;

    public void addFriends(Long userId, Long friendId) {
        String query =
                """
                INSERT
                INTO friendship (following_user_id, followed_user_id)
                VALUES (?, ?)
                """;
        jdbc.update(query, userId, friendId);
    }

    public void removeFriends(Long userId, Long friendId) {
        String query =
                """
                DELETE
                FROM friendship
                WHERE following_user_id = ? AND followed_user_id = ?
                """;
        jdbc.update(query, userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        String query =
                """
                SELECT *
                FROM friendship AS f
                INNER JOIN users AS u ON u.id = f.followed_user_id
                WHERE f.following_user_id = ?
                ORDER BY u.id
                """;
        return jdbc.query(query, new UserRowMapper(), userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        String query =
                """
                SELECT *
                FROM users
                WHERE id IN (
                    SELECT f.followed_user_id
                    FROM friendship AS f
                    JOIN friendship AS f2 ON f.followed_user_id = f2.followed_user_id
                    WHERE f.following_user_id = ? AND f2.following_user_id = ?
                    )
                """;
        return jdbc.query(query, new UserRowMapper(), userId, friendId);
    }

    private Boolean checkUserInFriends(Long userId, Long friendId) {
        String sql =
                """
                SELECT *
                FROM friendship
                WHERE following_user_id = ? AND followed_user_id = ?
                """;
        if (jdbc.queryForList(sql, userId, friendId).isEmpty()) {
            throw new NotFoundException("Пользователя с id " + friendId + " нет в друзьях у " + userId);
        }
        return true;
    }

}