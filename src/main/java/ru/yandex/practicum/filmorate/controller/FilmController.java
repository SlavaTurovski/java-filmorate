package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate realiseDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        log.info("Добавление фильма [{}]", newFilm.getName());
        if (newFilm.getReleaseDate().isBefore(realiseDate)) {
            log.warn("Дата фильма указана ранее минимальной. Фильм не создан");
            throw new ValidationException("Введены неверные данные о фильме");
        }
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм [{}] добавлен, присвоен id [{}]", newFilm.getName(), newFilm.getId());
        log.debug("Фильм [{}]", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Обновление фильма с id [{}]", updatedFilm.getId());
        if (!films.containsKey(updatedFilm.getId())) {
            log.warn("Фильм с id [{}] не найден", updatedFilm.getId());
            throw new ValidationException("Фильм с id [" + updatedFilm.getId() + "] не найден");
        }

        if (updatedFilm.getReleaseDate().isBefore(realiseDate)) {
            log.warn("Дата фильма указана ранее минимальной");
            throw new ValidationException("Введены неверные данные о фильме");
        }

        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Фильм с id [{}] обновлён", updatedFilm.getId());
        log.debug("Фильм [{}]", updatedFilm);
        return updatedFilm;
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