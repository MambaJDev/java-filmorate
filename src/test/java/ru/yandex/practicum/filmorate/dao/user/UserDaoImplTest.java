package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
}