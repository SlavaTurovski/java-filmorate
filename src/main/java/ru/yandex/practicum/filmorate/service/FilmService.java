package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

   private final FilmStorage filmStorage;
   private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film newFilm) {
        return filmStorage.createFilm(newFilm);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.updateFilm(updatedFilm);
    }

    public Optional<Film> getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long userId, Long filmId) {
        Optional<Film> film1 = filmStorage.getFilmById(filmId);
        Optional<User> user1 = userStorage.getUserById(userId);
        if (film1.isEmpty() || user1.isEmpty()) {
            throw new NotFoundException("не найден");
        }
        Film film = film1.get();
        film.getLikes().add(userId);
    }

    public void removeLike(Long userId, Long filmId) {
        Optional<Film> film1 = filmStorage.getFilmById(filmId);
        Optional<User> user1 = userStorage.getUserById(userId);
        if (film1.isEmpty() || user1.isEmpty()) {
            throw new NotFoundException("Пользователь либо фильм не найден!");
        }
        Film film = film1.get();
        film.getLikes().remove(userId);
        throw new NotFoundException("Лайк пользователя не найден!");
    }

    public List<Film> getTopPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}