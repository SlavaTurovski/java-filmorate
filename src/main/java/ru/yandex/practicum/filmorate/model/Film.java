package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"id","description"})
public class Film {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Long> likes = new HashSet<>();

}