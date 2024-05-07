package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Disabled
class UserDaoImplTest {
    private final JdbcTemplate jdbcTemplate;

    private User userForTest() {
        return new User()
                .setId(1L)
                .setEmail("user@email.ru")
                .setName("Ivan Petrov")
                .setLogin("vanya123")
                .setBirthday("1990-01-01");
    }

    @Test
    void getUserById() {
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);

        final User savedUser = userDao.getUserById(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user1);
    }

    @Test
    void addUserToDatabase() {
        final User user = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user);

        final List<User> savedList = userDao.getAll();

        assertThat(savedList.size())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(1);
    }

    @Test
    void updateUser() {
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        userDao.update(new User().setId(1L).setLogin("Dima").setEmail("mamba84@mail.ru"));
        final User savedUser = userDao.getUserById(1L);

        assertThat(savedUser.getLogin())
                .usingDefaultComparator()
                .isEqualTo("Dima");
    }

    @Test
    void deleteUser() {
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        userDao.delete(user1);

        Assertions.assertThrows(EmptyResultDataAccessException.class,
                () -> userDao.getUserById(1L));
    }

    @Test
    void getAllUsers() {
        final User newUser = userForTest();
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
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = new User().setId(2L).setLogin("King").setEmail("hello@google.com");
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
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = new User().setId(2L).setLogin("King").setEmail("hello@google.com");
        final User user3 = new User().setId(3L).setLogin("Ken").setEmail("he@google.com");
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
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = new User().setId(2L).setLogin("King").setEmail("hello@google.com");
        final User user3 = new User().setId(3L).setLogin("Ken").setEmail("he@google.com");
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
        final User user1 = userForTest();
        final UserDao userDao = new UserDaoImpl(jdbcTemplate);
        userDao.add(user1);
        final User user2 = new User().setId(2L).setLogin("King").setEmail("hello@google.com");
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