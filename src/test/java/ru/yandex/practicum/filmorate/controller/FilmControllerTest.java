package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film();
         film.setId(1L);
         film.setName("Иван Васильевич меняет профессию");
         film.setDescription("Комедия");
         film.setReleaseDate(LocalDate.of(1973, 9, 17));
         film.setDuration(93L);
    }

    @Test
    void shouldReturnFilms() {
        filmController.createFilm(film);
        Collection<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
    }

    @Test
    void shouldCreateFilm() {
        Film added = filmController.createFilm(film);
        assertNotNull(added.getId());
        assertEquals("Иван Васильевич меняет профессию", added.getName());
    }

    @Test
    void shouldUpdateFilm() {
        Film added = filmController.createFilm(film);
        added.setName("Маска");
        Film updated = filmController.updateFilm(added);
        assertEquals("Маска", updated.getName());
    }

    @Test
    void shouldThrowExceptionForWrongDate() {
        film.setReleaseDate(LocalDate.of(1111, 1, 1));
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Введены неверные данные о фильме", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentId() {
        film.setId(1111L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
        assertEquals("Фильм с id [1111] не найден", ex.getMessage());
    }

}