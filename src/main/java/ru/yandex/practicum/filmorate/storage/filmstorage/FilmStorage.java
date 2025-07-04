package ru.yandex.practicum.filmorate.storage.filmstorage;

import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film createFilm(Film newFilm) throws ValidationException;

    Film updateFilm(Film updatedFilm) throws ValidationException;

    Optional<Film> getFilmById(Long filmId) throws ValidationException;

    Collection<Film> getTopFilms(int limit) throws InternalServerException;

}