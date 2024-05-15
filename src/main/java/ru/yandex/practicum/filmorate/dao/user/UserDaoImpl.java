package ru.yandex.practicum.filmorate.dao.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            jdbcTemplate.update("delete from users where id = ?", id);
            jdbcTemplate.update("delete from films_users where user_id = ?", id);
            jdbcTemplate.update("delete from friends where user_id = ?", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя из БД");
            throw new NotFoundException("Ошибка при удалении пользователя из БД");
        }
    }

    @Override
    public void deleteAllUsers() {
        try {
            jdbcTemplate.update("delete from users");
            jdbcTemplate.update("delete from films_users");
            jdbcTemplate.update("delete from friends");
        } catch (Exception e) {
            log.error("Ошибка при удалении всех пользователей из БД");
            throw new NotFoundException("Ошибка при удалении всех пользователей из БД");
        }
    }

    @Override
    public User update(User user) {
        getUserById(user.getId());
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
        try {
            User user = jdbcTemplate.queryForObject("select * from users where id = ?", userRowMapper(), id);
            log.info("Юзер успешно получен по ID = {}", id);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь не найден" + id);
        }
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        getUserById(friendId);
        createFeedHistory(userId, EventType.FRIEND, Operation.ADD, friendId);

        if (jdbcTemplate.update("insert into friends (user_id, friend_id) values (?, ?);", userId, friendId) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        log.info("Пользователь c id = {} стал другом пользователю {}", friendId, userId);
        return user;
    }

    @Override
    public List<User> getAllFriends(Long id) {
        getUserById(id);
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
        getUserById(userId);
        getUserById(friendId);
        createFeedHistory(userId, EventType.FRIEND, Operation.REMOVE, friendId);

        if (jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", userId, friendId) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        log.info("Пользователь c id = {} удалился из друзей пользователя {}", friendId, userId);
    }

    @Override
    public List<Feed> getFeedHistory(Long id) {
        getUserById(id);
        String sqlQuery = "select fh.id, fh.user_id, fh.create_time, et.name as event_type_name, o.name as operation_name, fh.entity_id " +
                "from feed_history as fh " +
                "join event_types as et on fh.event_type_id = et.id " +
                "join operations as o on fh.operation_id = o.id " +
                "where fh.user_id = ?";

        log.info("Получена история пользователя с id: {}", id);
        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, id);
    }

    @Override
    public void createFeedHistory(Long userId, EventType eventType, Operation operation, Long entityId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed_history")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", userId);
        values.put("create_time", Instant.now().truncatedTo(ChronoUnit.MICROS));
        values.put("event_type_id", eventType.getIndex());
        values.put("operation_id", operation.getIndex());
        values.put("entity_id", entityId);

        log.info("Добавлена новая запись в историю у пользователя: {}", userId);
        simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(values));
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

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getLong("id"))
                .timestamp(resultSet.getTimestamp("create_time").getTime())
                .userId(Long.valueOf(resultSet.getString("user_id")))
                .eventType(EventType.valueOf((resultSet.getString("event_type_name"))))
                .operation(Operation.valueOf(resultSet.getString("operation_name")))
                .entityId(Long.valueOf(resultSet.getString("entity_id")))
                .build();
    }
}