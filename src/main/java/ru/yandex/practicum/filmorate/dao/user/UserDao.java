package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface UserDao {

    User add(User user);

    void deleteUserById(Integer id);

    void deleteAllUsers();

    User update(User user);

    User getUserById(Long id);

    List<User> getAll();

    User addFriend(Long userID, Long friendID);

    void deleteFriend(Long userID, Long friendID);

    List<User> getAllFriends(Long id);

    List<User> getCommonFriends(Long userID, Long otherID);

    List<Feed> getFeedHistory(Long id);

    void createFeedHistory(Long userId, EventType eventType, Operation operation, Long entityId);
}