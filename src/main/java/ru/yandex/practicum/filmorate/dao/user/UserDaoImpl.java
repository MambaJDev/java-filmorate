package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (user.getName().isBlank()) user.setName(user.getLogin());
        String sqlQuery = "insert into users(name, email, login, birthday) values (?, ?, ?, ?)";
        if (jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()) == 0) {
            log.info("Операция обновления данных юзера в БД закончилась неудачей");
        }
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("select * from users where login = ?", user.getLogin());
        while (userRow.next()) {
            user.setId(userRow.getLong("id"));
        }
        log.info("Юзеру присвоен ID = {}", user.getId());
        log.info("Юзер с именем {} и ID {} успешно добавлен", user.getName(), user.getId());
        return user;
    }

    @Override
    public void deleteUserById(Integer id) {
        try {
            String sql = "delete from users where id = ?";
            jdbcTemplate.update(sql, id);
            String sql2 = "delete from films_users where user_id = ?";
            jdbcTemplate.update(sql2, id);
            String sql3 = "delete from friends where user_id = ?";
            jdbcTemplate.update(sql3, id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя из БД");
            throw new NotFoundException("Ошибка при удалении пользователя из БД");
        }
    }

    @Override
    public void deleteAllUsers() {
        try {
            String sql = "delete from users";
            jdbcTemplate.update(sql);
            String sql2 = "delete from films_users";
            jdbcTemplate.update(sql2);
            String sql3 = "delete from friends";
            jdbcTemplate.update(sql3);
        } catch (Exception e) {
            log.error("Ошибка при удалении всех пользователей из БД");
            throw new NotFoundException("Ошибка при удалении всех пользователей из БД");
        }
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update users set name = ?, email = ?, login = ?, birthday = ?  where id = ?";
        if (jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()) == 0) {
            log.info("Операция обновления данных юзера в БД закончилась неудачей");
        }
        log.info("Юзер с именем {} и ID {} успешно обновлен", user.getName(), user.getId());
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("select * from users", userRowMapper());
        log.info("Список всех юзеров получен из БД, размер списка = {}", users.size());
        return users;
    }

    @Override
    public User getUserById(Long id) {
        User user = jdbcTemplate.queryForObject("select * from users where id = ?", userRowMapper(), id);
        log.info("Юзер успешно получен по ID = {}", id);
        return user;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        user.getFriends().add(friendId);
        if (jdbcTemplate.update("insert into friends (user_id, friend_id) values (?, ?);", userId, friendId) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        log.info("Пользователь c id = {} стал другом пользователю {}", friendId, userId);
        return user;
    }

    @Override
    public List<User> getAllFriends(Long id) {
        List<User> users = jdbcTemplate.query("select * from users where id in (select friend_id " +
                "from friends where user_id = ?)", userRowMapper(), id);
        log.info("Список друзей юзера {} получен из БД, размер списка = {}", id, users.size());
        return users;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        List<User> listFriends1 = getAllFriends(userId);
        List<User> listFriends2 = getAllFriends(otherId);
        List<User> users = listFriends1.stream()
                .filter(listFriends2::contains)
                .collect(Collectors.toList());
        log.info("Список общих друзей юзера {} и юзера {} получен из БД, размер списка = {}", userId, otherId, users.size());
        return users;
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        user.getFriends().remove(friendId);
        if (jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", userId, friendId) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        getUserById(userId).getFriends().remove(friendId);
        log.info("Пользователь c id = {} удалился из друзей пользователя {}", friendId, userId);
    }

    private RowMapper<User> userRowMapper() {
        return ((rs, rowNum) -> new User()
                .setId(rs.getLong("id"))
                .setName(rs.getString("name"))
                .setEmail(rs.getString("email"))
                .setLogin(rs.getString("login"))
                .setBirthday(rs.getString("birthday"))
                .setFriends(setFriendsListFromDb(rs.getLong("id")))
        );
    }

    private List<Long> setFriendsListFromDb(Long id) {
        List<Long> friends = new ArrayList<>();
        String sql = "select friend_id from friends where user_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            friends.add(sqlRowSet.getLong("friend_id"));
        }
        return friends;
    }
}