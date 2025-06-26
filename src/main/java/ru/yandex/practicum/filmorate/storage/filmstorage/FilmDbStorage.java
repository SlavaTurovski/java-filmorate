package ru.yandex.practicum.filmorate.storage.filmstorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    @Autowired
    protected JdbcTemplate jdbc;

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name AS name_mpa " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
            "WHERE f.id = ? ";

    private static final String FIND_ALL_QUERY =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name AS name_mpa " +
            "FROM films AS f " +
            "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id ";

    private static final String INSERT_QUERY =
            "INSERT " +
            "INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?) ";

    private static final String UPDATE_QUERY =
            "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
            "WHERE id = ? ";

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        try {
            Film result = jdbc.queryForObject(FIND_BY_ID_QUERY, new FilmRowMapper(), filmId);
            String genreSql =
                    "SELECT g.genre_id, g.name " +
                    "FROM film_genre AS fg " +
                    "LEFT JOIN films AS f ON f.id = fg.film_id " +
                    "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                    "WHERE f.id = ? ";
            List<Genre> genre = jdbc.query(genreSql, new GenreRowMapper(), filmId);
            result.setGenres(new LinkedHashSet<>(genre));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Получение списка фильмов");
        List<Film> result = jdbc.query(FIND_ALL_QUERY, new FilmRowMapper());
        String genreSql =
                "SELECT g.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "LEFT JOIN films AS f ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id WHERE f.id = ? ";
        for (Film film : result) {
            long id = film.getId();
            List<Genre> genre = jdbc.query(genreSql, new GenreRowMapper(), id);
            film.setGenres(new LinkedHashSet<>(genre));
        }
        return result;
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Добавление фильма [{}]", film.getName());
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка в названии фильма");
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка в описании фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов!");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Ошибка в дате выхода фильма");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года!");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка в продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            try {
                jdbc.update(connection -> {
                    PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, new String[]{"id"});
                    statement.setString(1, film.getName());
                    statement.setString(2, film.getDescription());
                    statement.setDate(3, Date.valueOf(film.getReleaseDate()));
                    statement.setLong(4, film.getDuration());
                    if (film.getMpa() != null) {
                        statement.setLong(5, film.getMpa().getId());
                    } else {
                        statement.setNull(5, Types.INTEGER);
                    }
                    return statement;
                }, keyHolder);
            } catch (Exception e) {
                throw new NotFoundException("Рейтинг не найден!");
            }
            film.setId(keyHolder.getKey().longValue());
            updateGenres(film);
            return film;
        } catch (InternalServerException ex) {
            log.error("Ошибка при создании пользователя: {}", ex.getMessage());
            throw new RuntimeException("Не удалось создать пользователя!");
        }
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма с id [{}]", updatedFilm.getId());
        if (updatedFilm.getId() == null) {
            throw new ValidationException("Для обновления фильма необходимо указать его id!");
        }
        Optional<Film> mayBeFilm = getFilmById(updatedFilm.getId());
        if (mayBeFilm.isPresent()) {
            Film oldFilm = mayBeFilm.get();
            if (updatedFilm.getName() != null) {
                if (updatedFilm.getName().isBlank()) {
                    log.error("Ошибка в названии при обновлении!");
                    throw new ValidationException("Название фильма не может быть пустым!");
                }
                oldFilm.setName(updatedFilm.getName());
                log.debug("Название изменено на {}", oldFilm.getName());
            }
            if (updatedFilm.getDescription() != null) {
                if (updatedFilm.getDescription().length() > 200) {
                    log.error("Ошибка в описании при обновлении!");
                    throw new ValidationException("Максимальная длина описания — 200 символов!");
                }
                oldFilm.setDescription(updatedFilm.getDescription());
                log.debug("Описание изменено на {}", oldFilm.getDescription());
            }
            if (updatedFilm.getReleaseDate() != null) {
                if (updatedFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
                    log.error("Ошибка в дате выхода при обновлении!");
                    throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года!");
                }
                oldFilm.setReleaseDate(updatedFilm.getReleaseDate());
                log.debug("Дата выхода изменена на {}", oldFilm.getReleaseDate());
            }

            if (updatedFilm.getDuration() > 0) {
                oldFilm.setDuration(updatedFilm.getDuration());
                log.debug("Продолжительность изменена на {}", oldFilm.getDuration());
            }
            if (updatedFilm.getMpa() != null) {
                oldFilm.setMpa(updatedFilm.getMpa());
                log.debug("id рейтинга изменен на {}", oldFilm.getMpa());
            }
            int rowsUpdated = jdbc.update(UPDATE_QUERY, oldFilm.getName(), oldFilm.getDescription(),
                    oldFilm.getReleaseDate(), oldFilm.getDuration(), oldFilm.getMpa().getId(), oldFilm.getId());
            if (rowsUpdated == 0) {
                throw new InternalServerException("Не удалось обновить данные!");
            }
            String sqlQueryForDeleteGenres =
                            "DELETE FROM film_genre " +
                            "WHERE FILM_ID = ? ";
            jdbc.update(sqlQueryForDeleteGenres, oldFilm.getId());
            updateGenres(oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id " + updatedFilm.getId()  + " не найден!");
    }

    private void updateGenres(Film film) {
        log.info("Обновление жанров");
        try {
            if (film.getGenres() != null) {
                String sqlQueryForGenres =
                                "INSERT " +
                                "INTO film_genre (FILM_ID, GENRE_ID) " +
                                "VALUES (?, ?) ";
                jdbc.batchUpdate(
                        sqlQueryForGenres, film.getGenres(), film.getGenres().size(),
                        (ps, genre) -> {
                            ps.setLong(1, film.getId());
                            ps.setLong(2, genre.getId());
                        });
            } else film.setGenres(new LinkedHashSet<>());
        } catch (Exception e) {
            throw new NotFoundException("Жанр не найден!");
        }
    }

    @Override
    public Collection<Film> getTopFilms(int limit) {
        String sql = FIND_ALL_QUERY +
                "LEFT JOIN film_likes AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ? ";
        return jdbc.query(sql, new FilmRowMapper(), limit);
    }

}