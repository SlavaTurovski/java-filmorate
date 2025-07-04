package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserDbStorage;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final LikeDbStorage likeStorage;

    public Collection<Film> getFilms() {
        log.info("Поиск фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        log.info("Поиск фильма по id");
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден!"));
    }

    public Film createFilm(Film newFilm) {
        log.info("Создание фильма");
        return filmStorage.createFilm(newFilm);
    }

    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление данных фильма");
        return filmStorage.updateFilm(updatedFilm);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка");
        Optional<Film> mayBeFilm = filmStorage.getFilmById(filmId);
        Optional<User> mayBeUser = userStorage.getUserById(userId);
        if (mayBeFilm.isEmpty() || mayBeUser.isEmpty()) {
          throw new NotFoundException("Фильм или пользователь не найдены!");
        }
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка");
        Optional<Film> mayBeFilm = filmStorage.getFilmById(filmId);
        Optional<User> mayBeUser = userStorage.getUserById(userId);
        if (mayBeFilm.isEmpty() || mayBeUser.isEmpty()) {
            throw new NotFoundException("Фильм или пользователь не найдены!");
        }
        likeStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        log.info("Получение списка популярных фильмов");
        return filmStorage.getTopFilms(count);
    }

}