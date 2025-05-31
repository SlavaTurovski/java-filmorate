package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<DateValidatorAnnotation, LocalDate> {
    private static final LocalDate BEFORE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(DateValidatorAnnotation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(BEFORE_DATE);
    }

}