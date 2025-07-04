package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("mpaDbStorage")
public class MpaDbStorage {

    @Autowired
    private JdbcTemplate jdbc;

    public Optional<Mpa> getMpaById(Long id) {
        try {
            String sqlQuery =
                    """
                    SELECT *
                    FROM mpa
                    WHERE mpa_id = ?
                    """;

            return Optional.ofNullable(jdbc.queryForObject(sqlQuery, new MpaRowMapper(), id));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public Collection<Mpa> getAllMpa() {
        String sqlQuery =
                """
                SELECT *
                FROM mpa
                """;
        return jdbc.query(sqlQuery, new MpaRowMapper());
    }

}