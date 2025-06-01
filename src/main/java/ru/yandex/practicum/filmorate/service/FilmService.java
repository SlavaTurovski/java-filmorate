package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;

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
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка");
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);
        film.getLikes().add(user.getId());
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка");
        Film film = getFilmById(filmId);
        if (userService.getUserById(userId) != null) {
            film.getLikes().remove(userId);
        }
    }

    public List<Film> getMostPopularFilms(Integer count) {
        log.info("Получение списка популярных фильмов");
        List<Film> films = filmStorage.getFilms();
        return films.stream()
                .sorted((film1, film2) ->
                        Integer.compare((film2.getLikes() != null) ? film2.getLikes().size() : 0,
                                (film1.getLikes() != null) ? film1.getLikes().size() : 0))
                .limit(count)
                .collect(Collectors.toList());
    }

}