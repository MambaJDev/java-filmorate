package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.dao.user.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Disabled
class FilmDaoImplTest {
    private final JdbcTemplate jdbcTemplate;

    private Film film1ForTest() {
        return new Film()
                .setId(1L)
                .setName("Man in black")
                .setDescription("American sci-fi action comedy film")
                .setReleaseDate("1997-07-02")
                .setDuration(98L)
                .setMpa(new Mpa(3L, "PG-13"))
                .setGenres(List.of(new Genre(1L, "Комедия")));
    }

    private Film film2ForTest() {
        return new Film()
                .setId(2L)
                .setName("Friends")
                .setDescription("American comedy film")
                .setReleaseDate("2000-10-02")
                .setDuration(50L)
                .setMpa(new Mpa(3L, "PG-13"))
                .setGenres(List.of(new Genre(1L, "Комедия")));
    }

    @Test
    void addFilmToDatabase() {
        final Film film1 = film1ForTest();
        final Film film2 = film2ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        filmDao.add(film2);
        final List<Film> savedList = filmDao.getAll();

        assertThat(savedList.size())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(2);
    }

    @Test
    void updateFilm() {
        final Film film1 = film1ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        filmDao.update(new Film()
                .setId(1L)
                .setName("Women in Red")
                .setDescription("American sci-fi action comedy film")
                .setReleaseDate("1997-07-02")
                .setDuration(98L)
                .setMpa(new Mpa(3L, "PG-13"))
                .setGenres(List.of(new Genre(1L, "Комедия"))));

        Film savedFilm = filmDao.getFilmById(1L);

        assertThat(savedFilm.getName())
                .usingDefaultComparator()
                .isEqualTo("Women in Red");
    }

    @Test
    void deleteFilm() {
        final Film film1 = film1ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        filmDao.deleteFilmById(Math.toIntExact(film1.getId()));
        Assertions.assertThrows(EmptyResultDataAccessException.class,
                () -> filmDao.getFilmById(film1.getId()));
    }

    @Test
    void getFilmById() {
        final Film film1 = film1ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        final Film savedFilm = filmDao.getFilmById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    void getAllFilms() {
        final Film film1 = film1ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        final List<Film> newFilmList = List.of(film1);
        final List<Film> savedFilmList = filmDao.getAll();

        assertThat(savedFilmList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilmList);
    }

    @Test
    void getPopularFilms() {
        final Film film1 = film1ForTest();
        final Film film2 = film2ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        filmDao.add(film2);
        final User user1 = new User().setId(2L).setLogin("King").setEmail("hello@google.com");
        final User user2 = new User().setId(3L).setLogin("Ken").setEmail("he@google.com");
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        userDao.add(user2);
        filmDao.addLike(film2.getId(), user1.getId());
        filmDao.addLike(film2.getId(), user2.getId());
        filmDao.addLike(film1.getId(), user2.getId());

        final List<Film> savedFilmList = filmDao.getPopularFilms(1, null, null);

        assertThat(savedFilmList.size())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(1);

        assertThat(savedFilmList.get(0).getId())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film2.getId());
    }

    @Test
    void addLikeToFilm() {
        final Film film1 = film1ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        final User user1 = new User().setLogin("King").setEmail("hello@google.com");
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        filmDao.addLike(film1.getId(), user1.getId());
        int likes = filmDao.getFilmById(film1.getId()).getLikes();

        assertThat(likes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(1);
    }

    @Test
    void deleteLikeFromFilm() {
        final Film film1 = film1ForTest();
        final FilmDao filmDao = new FilmDaoImpl(jdbcTemplate);
        filmDao.add(film1);
        final User user1 = new User().setLogin("King").setEmail("hello@google.com");
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        filmDao.addLike(film1.getId(), user1.getId());
        filmDao.deleteLike(film1.getId(), user1.getId());
        int likes = filmDao.getFilmById(film1.getId()).getLikes();

        assertThat(likes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(0);
    }
}