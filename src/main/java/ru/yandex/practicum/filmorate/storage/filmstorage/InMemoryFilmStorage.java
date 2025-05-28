package ru.yandex.practicum.filmorate.storage.filmstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        log.info("Получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film newFilm) {
        log.info("Добавление фильма [{}]", newFilm.getName());
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм [{}] добавлен, присвоен id [{}]", newFilm.getName(), newFilm.getId());
        log.debug("Фильм [{}]", newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма с id [{}]", updatedFilm.getId());
        if (updatedFilm.getId() == null) {
            throw new ValidationException("Для обновления фильма необходимо указать id");
        }
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
            log.info("Фильм с id [{}] обновлен", updatedFilm.getId());
            log.debug("Фильм [{}]", updatedFilm);
            return updatedFilm;
        }
        throw new NotFoundException("Фильм с id " + updatedFilm.getId()  + " не найден");
    }

    @Override
    public Film getFilmById(Long filmId) {
        return films.get(filmId);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}