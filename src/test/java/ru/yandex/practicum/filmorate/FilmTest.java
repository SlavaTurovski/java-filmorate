package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.filmstorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class,
        FilmRowMapper.class,
        MpaRowMapper.class,
        MpaDbStorage.class,
        GenreDbStorage.class})

public class FilmTest {

    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private static Film film;
    private final Mpa mpa = Mpa.builder().id(1L).name("G").build();
    private Long i = 0L;

    @BeforeEach
    public void setUp() {
        film = Film.builder()
                .name("dfff")
                .duration(100)
                .description("dfgdergdf")
                .releaseDate(LocalDate.parse("1999-11-11"))
                .mpa(mpa)
                .build();
        i++;
    }

    @Test
    public void shouldAddTheMainDataInFilm() {
        Film createFilm = filmDbStorage.createFilm(film);
        Collection<Film> allFilms = filmDbStorage.getFilms();
        assertEquals(allFilms.size(), i, "Фильм не был создан");
        assertThat(createFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(createFilm).hasFieldOrPropertyWithValue("name", film.getName());
        assertThat(createFilm).hasFieldOrPropertyWithValue("description", film.getDescription());
        assertThat(createFilm).hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate());
        assertThat(createFilm).hasFieldOrPropertyWithValue("genres", (new LinkedHashSet<>()));
        assertThat(createFilm).hasFieldOrPropertyWithValue("mpa", mpa);

    }

    @Test
    void shouldCreateFilmWithGenres() {
        Genre genre1 = genreDbStorage.getGenreById(1L)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
        Genre genre2 = genreDbStorage.getGenreById(2L)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
        Genre genre3 = genreDbStorage.getGenreById(1L)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
        Collection<Genre> genres = Arrays.asList(genre1, genre2, genre3);
        film.setGenres(new LinkedHashSet<>(genres));
        Collection<Genre> resultGenres = Arrays.asList(genre1, genre2);
        Film createFilm = filmDbStorage.createFilm(film);
        Collection<Film> allFilms = filmDbStorage.getFilms();
        assertEquals(allFilms.size(), i, "Фильм не был создан");
        assertThat(createFilm).hasFieldOrPropertyWithValue("id", film.getId());
        assertThat(createFilm).hasFieldOrPropertyWithValue("name", film.getName());
        assertThat(createFilm).hasFieldOrPropertyWithValue("description", film.getDescription());
        assertThat(createFilm).hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate());
        assertEquals(resultGenres.size(), film.getGenres().size(), "количество жанров не совпадает");
        assertThat(createFilm).hasFieldOrPropertyWithValue("mpa", mpa);
    }

    @Test
    void shouldGetFilmById() {
        Film createdFilm = filmDbStorage.createFilm(film);
        Film foundFilm = filmDbStorage.getFilmById(createdFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));;
        assertThat(foundFilm).hasFieldOrPropertyWithValue("id", createdFilm.getId());
        assertThat(foundFilm).hasFieldOrPropertyWithValue("name", createdFilm.getName());
        assertThat(foundFilm).hasFieldOrPropertyWithValue("description", createdFilm.getDescription());
        assertThat(foundFilm).hasFieldOrPropertyWithValue("releaseDate",createdFilm.getReleaseDate());
        assertThat(foundFilm).hasFieldOrPropertyWithValue("mpa", mpa);
    }

}