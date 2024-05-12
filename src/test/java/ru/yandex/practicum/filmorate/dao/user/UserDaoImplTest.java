package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.film.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.reviews.ReviewsDao;
import ru.yandex.practicum.filmorate.dao.reviews.ReviewsDaoImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDaoImplTest {
    private final JdbcTemplate jdbcTemplate;

    private User user1ForTest() {
        return new User()
                .setId(1L)
                .setEmail("user@email.ru")
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

    private User user3ForTest() {
        return new User()
                .setId(3L)
                .setEmail("user3@email.ru")
                .setName("Name3")
                .setLogin("login3")
                .setBirthday("2003-01-01");
    }

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

    private Review review1ForTest() {
        return new Review(0, "Good", true, 5, 2, 1);
    }

    private Review review2ForTest() {
        return new Review(0, "Bad", false, 10, 1, 2);
    }


    @Test
    void addAndGetUser() {
        final User user = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user);

        assertThat(userDao.getUserById(1L))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void updateUser() {
        User user1 = user1ForTest();
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);

        User user2 = user2ForTest().setId(1L);
        userDao.update(user2);

        assertThat(userDao.getUserById(1L))
                .usingDefaultComparator()
                .isEqualTo(user2);
    }

    @Test
    void deleteUser() {
        final User user1 = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        userDao.deleteUserById(Math.toIntExact(user1.getId()));

        Assertions.assertThrows(NotFoundException.class,
                () -> userDao.getUserById(1L));
    }

    @Test
    void getAllUsers() {
        final User newUser = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(newUser);

        final List<User> newUsersList = List.of(newUser);
        final List<User> savedUserList = userDao.getAll();

        assertThat(savedUserList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUsersList);
    }

    @Test
    void addFriend() {
        final User user1 = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = user2ForTest();
        userDao.add(user2);

        userDao.addFriend(1L, 2L);
        final User userWithFriend = userDao.getUserById(1L);

        final List<Long> newList = List.of(2L);
        final List<Long> friensList = userWithFriend.getFriends();

        assertThat(friensList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newList);
    }

    @Test
    void getAllFriends() {
        final User user1 = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = user2ForTest();
        final User user3 = user3ForTest();
        userDao.add(user2);
        userDao.add(user3);
        userDao.addFriend(1L, user2.getId());
        userDao.addFriend(1L, user3.getId());

        final List<User> newList = List.of(user2, user3);
        final List<User> friendsList = userDao.getAllFriends(1L);

        assertThat(friendsList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newList);
    }

    @Test
    void getCommonFriends() {
        final User user1 = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = user2ForTest();
        final User user3 = user3ForTest();
        userDao.add(user2);
        userDao.add(user3);
        userDao.addFriend(1L, 2L);
        userDao.addFriend(3L, 2L);
        final List<User> commonFriends = userDao.getCommonFriends(1L, 3L);

        assertThat(commonFriends.get(0))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    void deleteFriend() {
        final User user1 = user1ForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = user2ForTest();
        userDao.add(user2);
        userDao.addFriend(1L, 2L);
        userDao.deleteFriend(1L, 2L);

        final List<Long> empty = new ArrayList<>();
        final List<Long> friends = userDao.getUserById(1L).getFriends();

        assertThat(friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(empty);
    }

    @Test
    void getFeedHistory() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDaoImpl(jdbcTemplate, userDao);
        ReviewsDao reviewsDao = new ReviewsDaoImpl(jdbcTemplate, filmDao, userDao);

        final User user1 = user1ForTest();
        final User user2 = user2ForTest();
        userDao.add(user1);
        userDao.add(user2);
        userDao.addFriend(user1.getId(), user2.getId());
        userDao.deleteFriend(user1.getId(), user2.getId());
        userDao.addFriend(user1.getId(), user2.getId());

        final Film film1 = film1ForTest();
        final Film film2 = film2ForTest();
        filmDao.add(film1);
        filmDao.add(film2);
        filmDao.addLike(film1.getId(), user1.getId());
        filmDao.addLike(film1.getId(), user2.getId());

        final Review review1 = review1ForTest();
        final Review review2 = review2ForTest();
        reviewsDao.createReview(review1);
        reviewsDao.createReview(review2);

        var user1Feed = userDao.getFeedHistory(1L);
        var user2Feed = userDao.getFeedHistory(2L);

        assertEquals(5, user1Feed.size());
        assertEquals(2, user2Feed.size());
    }
}