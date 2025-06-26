package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorage {

    private final JdbcTemplate jdbc;

    public Collection<Genre> getGenres() {
        String query =
                "SELECT * " +
                "FROM genre;";
        return jdbc.query(query, new GenreRowMapper());
    }

    public Optional<Genre> getGenreById(Long id) {
        try {
            String query =
                    "SELECT * " +
                    "FROM genre " +
                    "WHERE genre_id = ? ";
            return Optional.ofNullable(jdbc.queryForObject(query, new GenreRowMapper(), id));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Collection<Genre> getFilmGenres(Long filmId) {
        String query =
                "SELECT * " +
                "FROM genre " +
                "WHERE genres_id IN (" +
                    "SELECT genre_id " +
                    "FROM film_genre WHERE = ?" +
                ") ";
        return jdbc.query(query, new GenreRowMapper(), filmId);
    }

}