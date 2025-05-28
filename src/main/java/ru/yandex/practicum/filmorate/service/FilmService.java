package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден!");
        }
        return film;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        for (Film value : filmStorage.getFilms()) {
            if (film.getName().equals(value.getName())) {
                throw new ValidationException("Фильм с таким названием уже существует");
            }
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);
        film.getLikes().add(user.getId());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (userService.getUserById(userId) != null) {
            film.getLikes().remove(userId);
        }
    }

    public List<Film> getMostPopularFilms(Integer count) {
        List<Film> films = filmStorage.getFilms();
        return films.stream()
                .filter(film -> film.getLikes() != null && !film.getLikes().isEmpty())
                .sorted((film1, film2) ->
                        Integer.compare((film2.getLikes() != null) ? film2.getLikes().size() : 0,
                                (film1.getLikes() != null) ? film1.getLikes().size() : 0))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не заполнено");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание фильма не заполнено");
        }
        if (film.getDescription().length() >= 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

}