package ru.yandex.practicum.filmorate.storage.filmstorage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film createFilm(Film newFilm);

    Film updateFilm(Film updatedFilm);

    Film getFilmById(Long filmId);

}