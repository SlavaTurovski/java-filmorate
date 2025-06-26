package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage dbGenres;

    public Collection<Genre> getGenres() {
        return dbGenres.getGenres();
    }

    public Genre getGenreById(Long id) {
        return dbGenres.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден!"));
    }

}