package ru.yandex.practicum.filmorate.dao.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GenreDaoImplTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testGetAllGenres() {
        final List<Genre> newList = List.of(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма"),
                new Genre(3L, "Мультфильм"),
                new Genre(4L, "Триллер"),
                new Genre(5L, "Документальный"),
                new Genre(6L, "Боевик")
        );

        final GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        final List<Genre> savedList = genreDao.getAllGenres();

        assertThat(savedList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newList);
    }

    @Test
    void testGetGenreById() {
        final Genre newGenre = new Genre(3L, "Мультфильм");
        final GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        final Genre savedGenre = genreDao.getGenreById(3L);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newGenre);
    }
}