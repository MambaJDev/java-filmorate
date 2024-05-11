package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.director.DirectorDao;
import ru.yandex.practicum.filmorate.dao.director.DirectorDaoImpl;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.dao.user.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
                .setGenres(List.of(new Genre(2L, "Драма")));
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

    private User user1ForTest() {
        return new User()
                .setId(1L)
                .setEmail("user1@email.ru")
                .setName("Ivan Petrov")
                .setLogin("vanya123")
                .setBirthday("1990-01-01");
    }

    private User user2ForTest() {
        return new User()
                .setId(2L)
                .setEmail("user2@email.ru")
                .setName("Name2")
                .setLogin("login2")
                .setBirthday("2000-01-01");
    }

    @Test
    void addFilmToDatabase() {
        final Film film1 = film1ForTest();
        final Film film2 = film2ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        filmDao.add(film1);
        filmDao.add(film2);

        assertThat(filmDao.getAll().size())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(2);
    }

    @Test
    void updateFilm() {
        final Film film1 = film1ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        filmDao.add(film1);

        Film film2 = film2ForTest().setId(1L);
        filmDao.update(film2);

        assertThat(filmDao.getFilmById(1L))
                .usingDefaultComparator()
                .isEqualTo(film2);
    }

    @Test
    void deleteFilm() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        final Film film1 = film1ForTest();
        final Film film2 = film2ForTest();
        filmDao.add(film1);
        filmDao.add(film2);
        filmDao.deleteFilmById(1);

        assertThat(filmDao.getAll())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film2));
    }

    @Test
    void getFilmById() {
        final Film film1 = film1ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        filmDao.add(film1);

        assertThat(filmDao.getFilmById(1L))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    void getAllFilms() {
        final Film film1 = film1ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        filmDao.add(film1);

        assertThat(filmDao.getAll())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1));
    }

    @Test
    void getPopularFilms() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);

        Film film1 = film1ForTest();
        Film film2 = film2ForTest();
        Film film3 = film2ForTest().setId(3L).setReleaseDate("1997-07-02");
        Film film4 = film1ForTest().setId(4L);
        filmDao.add(film1);
        filmDao.add(film2);
        filmDao.add(film3);
        filmDao.add(film4);

        User user1 = user1ForTest();
        User user2 = user2ForTest();
        User user3 = user2ForTest().setId(3L).setLogin("login3");
        userDao.add(user1);
        userDao.add(user2);
        userDao.add(user3);

        filmDao.addLike(1L, 1L);
        filmDao.addLike(1L, 2L);
        filmDao.addLike(1L, 3L);
        filmDao.addLike(2L, 1L);
        filmDao.addLike(2L, 2L);
        filmDao.addLike(3L, 1L);

        film1 = filmDao.getFilmById(1L);
        film2 = filmDao.getFilmById(2L);
        film3 = filmDao.getFilmById(3L);

        assertThat(filmDao.getPopularFilms(10, null, null))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1, film2, film3, film4));

        assertThat(filmDao.getPopularFilms(10, 1, null))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film2, film3));

        assertThat(filmDao.getPopularFilms(10, null, 1997))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1, film3, film4));

        assertThat(filmDao.getPopularFilms(10, 2, 1997))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1, film4));
    }

    @Test
    void addLikeToFilm() {
        final Film film1 = film1ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        filmDao.add(film1);
        final User user1 = user1ForTest();
        userDao.add(user1);
        filmDao.addLike(1L, 1L);

        assertThat(filmDao.getFilmById(1L).getUserIdLikes())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Set.of(1L));
    }

    @Test
    void deleteLikeFromFilm() {
        final Film film1 = film1ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        filmDao.add(film1);
        final User user1 = user1ForTest();
        userDao.add(user1);
        filmDao.addLike(1L, 1L);
        filmDao.deleteLike(1L, 1L);

        assertThat(filmDao.getFilmById(1L).getUserIdLikes())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Collections.emptySet());
    }

    @Test
    void deleteFilmById() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);

        Film film1 = film1ForTest();
        filmDao.add(film1);
        Film film2 = film2ForTest();
        filmDao.add(film2);
        filmDao.deleteFilmById(1);

        assertThat(filmDao.getAll())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film2));
    }

    @Test
    void deleteAllFilms() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);

        Film film1 = film1ForTest();
        filmDao.add(film1);
        Film film2 = film2ForTest();
        filmDao.add(film2);
        filmDao.deleteAllFilms();

        assertThat(filmDao.getAll())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void getFilmsByDirector() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        DirectorDao directorDao = new DirectorDaoImpl(jdbcTemplate);

        Director director1 = new Director(1, "director1");
        Director director2 = new Director(2, "director2");
        directorDao.addDirector(director1);
        directorDao.addDirector(director2);

        Film film1 = film1ForTest().setDirectors(Set.of(director1));
        Film film2 = film2ForTest().setDirectors(Set.of(director2));
        Film film3 = film2ForTest().setId(3L).setDirectors(Set.of(director1));
        filmDao.add(film1);
        filmDao.add(film2);
        filmDao.add(film3);

        User user1 = user1ForTest();
        User user2 = user2ForTest();
        userDao.add(user1);
        userDao.add(user2);

        filmDao.addLike(1L, 1L);
        filmDao.addLike(1L, 2L);
        filmDao.addLike(3L, 1L);
        film1 = filmDao.getFilmById(1L);
        film3 = filmDao.getFilmById(3L);

        assertThat(filmDao.getFilmsByDirector("likes", 1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film3, film1));

        assertThat(filmDao.getFilmsByDirector("year", 1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1, film3));
    }

    @Test
    void getCommonFilms() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);

        Film film1 = film1ForTest();
        Film film2 = film2ForTest();
        Film film3 = film2ForTest().setId(3L);
        filmDao.add(film1);
        filmDao.add(film2);
        filmDao.add(film3);

        User user1 = user1ForTest();
        User user2 = user2ForTest();
        User user3 = user2ForTest().setId(3L).setLogin("login3");
        userDao.add(user1);
        userDao.add(user2);
        userDao.add(user3);

        filmDao.addLike(1L, 1L);
        filmDao.addLike(1L, 2L);
        filmDao.addLike(1L, 3L);
        filmDao.addLike(2L, 1L);
        filmDao.addLike(3L, 2L);
        filmDao.addLike(3L, 1L);

        film1 = filmDao.getFilmById(1L);
        film3 = filmDao.getFilmById(3L);

        assertThat(filmDao.getCommonFilms(1L, 2L))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1, film3));

        assertThat(filmDao.getCommonFilms(1L, 3L))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1));
    }
}