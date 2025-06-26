package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeDbStorage {

    @Autowired
    private final JdbcTemplate jdbc;

    public void addLike(long filmId, long userId) {
        String sql =
                "INSERT " +
                "INTO film_likes (film_id, user_id) " +
                "VALUES (?, ?) ";
        jdbc.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql =
                "DELETE " +
                "FROM film_likes " +
                "WHERE film_id = ? AND user_id = ? ";
        jdbc.update(sql, filmId, userId);
    }
}
