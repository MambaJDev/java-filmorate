package ru.yandex.practicum.filmorate.dao.reviews;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.film.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.dao.user.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewsLikeDaoImplTest {

    private final JdbcTemplate jdbcTemplate;

    private ReviewsDao setReviewsDaoBeforeTest() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        User user = new User()
                .setId(1L)
                .setEmail("user@email.ru")
                .setName("Ivan Petrov")
                .setLogin("vanya123")
                .setBirthday("1990-01-01");
        userDao.add(user);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        Film film = new Film()
                .setId(1L)
                .setName("Man in black")
                .setDescription("American sci-fi action comedy film")
                .setReleaseDate("1997-07-02")
                .setDuration(98L)
                .setMpa(new Mpa(3L, "PG-13"))
                .setGenres(List.of(new Genre(2L, "Драма")));
        filmDao.add(film);
        return new ReviewsDaoImpl(jdbcTemplate, filmDao, userDao);
    }

    @Test
    void addLike() {
        ReviewsDao reviewsDao = setReviewsDaoBeforeTest();
        Review review = new Review(1, "Отличный фильм", true, 0, 1, 1);
        reviewsDao.createReview(review);

        ReviewsLikeDao reviewsLikeDao = new ReviewsLikeDaoImpl(jdbcTemplate);
        reviewsLikeDao.addLike(1, 1);

        assertThat(reviewsDao.getReviewById(1).getUseful())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(1);
    }

    @Test
    void addDislike() {
        ReviewsDao reviewsDao = setReviewsDaoBeforeTest();
        Review review = new Review(1, "Отличный фильм", true, 0, 1, 1);
        reviewsDao.createReview(review);

        ReviewsLikeDao reviewsLikeDao = new ReviewsLikeDaoImpl(jdbcTemplate);
        reviewsLikeDao.addDislike(1, 1);

        assertThat(reviewsDao.getReviewById(1).getUseful())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(-1);
    }

    @Test
    void deleteLike() {
        ReviewsDao reviewsDao = setReviewsDaoBeforeTest();
        Review review = new Review(1, "Отличный фильм", true, 0, 1, 1);
        reviewsDao.createReview(review);

        ReviewsLikeDao reviewsLikeDao = new ReviewsLikeDaoImpl(jdbcTemplate);
        reviewsLikeDao.addLike(1, 1);

        reviewsLikeDao.deleteLike(1, 1);

        assertThat(reviewsDao.getReviewById(1).getUseful())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(0);
    }

    @Test
    void deleteDislike() {
        ReviewsDao reviewsDao = setReviewsDaoBeforeTest();
        Review review = new Review(1, "Отличный фильм", true, 0, 1, 1);
        reviewsDao.createReview(review);

        ReviewsLikeDao reviewsLikeDao = new ReviewsLikeDaoImpl(jdbcTemplate);
        reviewsLikeDao.addDislike(1, 1);

        reviewsLikeDao.deleteDislike(1, 1);

        assertThat(reviewsDao.getReviewById(1).getUseful())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(0);
    }
}