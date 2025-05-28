package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.filmstorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private UserController userController;

    private Film film;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage());
        userController = new UserController(userService);
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userService));
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
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
        assertEquals("Фильм с id 1111 не найден!", ex.getMessage());
    }

}