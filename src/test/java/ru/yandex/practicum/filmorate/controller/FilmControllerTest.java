package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.filmstorage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.userstorage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private UserController userController;

    private Film film1;
    private Film film2;
    private Film film3;
    private User user1;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage());
        userController = new UserController(userService);
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userService));

        film1 = new Film();
        film1.setId(1L);
        film1.setName("Иван Васильевич меняет профессию");
        film1.setDescription("Комедия комедийная");
        film1.setReleaseDate(LocalDate.of(1973, 9, 17));
        film1.setDuration(93L);

        film2 = new Film();
        film2.setId(2L);
        film2.setName("Пила");
        film2.setDescription("Ужас ужасный");
        film2.setReleaseDate(LocalDate.of(2004, 12, 16));
        film2.setDuration(103L);

        film3 = new Film();
        film3.setId(3L);
        film3.setName("Интерстеллар");
        film3.setDescription("Фантастика научная");
        film3.setReleaseDate(LocalDate.of(2014, 11, 6));
        film3.setDuration(169L);

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("nastya@gmail.com");
        user1.setLogin("Anastasiya");
        user1.setName("Anastasiya Kulagina");
        user1.setBirthday(LocalDate.of(1999,12,12));
    }

    @Test
    void shouldReturnFilms() {
        filmController.createFilm(film1);
        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
    }

    @Test
    void shouldCreateFilm() {
        Film added = filmController.createFilm(film1);
        assertNotNull(added.getId());
        assertEquals("Иван Васильевич меняет профессию", added.getName());
    }

    @Test
    void shouldUpdateFilm() {
        Film added = filmController.createFilm(film1);
        added.setName("Маска");
        Film updated = filmController.updateFilm(added);
        assertEquals("Маска", updated.getName());
    }

    @Test
    void shouldThrowExceptionForWrongDate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        film1.setReleaseDate(LocalDate.of(1111, 1, 1));
        Set<ConstraintViolation<Film>> validations = factory.getValidator().validate(film1);
        assertTrue(validations.size() == 1);
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistentId() {
        film1.setId(1111L);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film1));
        assertEquals("Фильм с id 1111 не найден", ex.getMessage());
    }

    @Test
    void shouldAddLikesToFilm() {
        filmController.createFilm(film1);
        userController.createUser(user1);
        filmController.addLike(1L, 1L);
        assertEquals(1, film1.getLikes().size());
    }

    @Test
    void shouldRemoveLikesFromFilm() {
        filmController.createFilm(film1);
        userController.createUser(user1);
        filmController.removeLike(1L, 1L);
        assertEquals(0, film1.getLikes().size());
    }

    @Test
    void shouldGetMostPopularFilms() {
        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film3);
        userController.createUser(user1);
        filmController.addLike(1L, 1L);
        filmController.addLike(2L, 1L);
        filmController.addLike(3L, 1L);
        filmController.getMostPopularFilms(10);
        assertEquals(3, filmController.getMostPopularFilms(10).size());
        filmController.removeLike(3L, 1L);
        assertEquals(3, filmController.getMostPopularFilms(10).size());
    }

}