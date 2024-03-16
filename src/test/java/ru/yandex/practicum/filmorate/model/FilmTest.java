package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    private Validator validator;
    private final Film film = Film.builder()
            .id(1)
            .name("The Mask")
            .description("The Mask is a 1994 American superhero comedy film directed by Chuck Russell " +
                    "and produced by Bob Engelman from a screenplay by Mike Werb and a story by Michael Fallon " +
                    "and Mark Verheiden.")
            .releaseDate("1994-07-29")
            .duration(97)
            .build();

    @BeforeEach
    public void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void passValidatedFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void failValidateFilmWithEmptyName() {
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void failValidateFilmWithNullName() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void passValidateFilmWith200CharsDescription() {
        String description = String.valueOf(new char[200]);
        film.setDescription(description);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void failValidateFilmWith201CharsDescription() {
        String description = String.valueOf(new char[201]);
        film.setDescription(description);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void passValidateFilmWith199CharsDescription() {
        String description = String.valueOf(new char[199]);
        film.setDescription(description);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void failValidateFilmWithReleaseBefore28December1895() {
        film.setReleaseDate("1895-12-27");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void passValidateFilmWithReleaseAt28December1895() {
        film.setReleaseDate("1895-12-28");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void passValidateFilmWithReleaseAfter28December1895() {
        film.setReleaseDate("1895-12-29");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void failValidateFilmWithNegativeDuration() {
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void failValidateFilmWithZeroDuration() {
        film.setDuration(0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size(), "Film не прошел валидацию");
    }

    @Test
    void passValidateFilmWithPositiveDuration() {
        film.setDuration(1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Film не прошел валидацию");
    }
}