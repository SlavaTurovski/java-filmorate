package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Builder
@Data
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size(min = 1, max = 200)
    private String description;

    @NotNull
    @DateValidatorAnnotation
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
    private LinkedHashSet<Genre> genres;
    private Mpa mpa;

}