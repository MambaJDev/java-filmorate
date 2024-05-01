package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User delete(User user);

    User update(User user);

    List<User> getAll();

    User getUserById(Long id);

    User addFriend(Long userId, Long friendId);

    List<User> getAllFriends(Long id);

    List<User> getCommonFriends(Long userId, Long otherId);

    void deleteFriend(Long userId, Long friendId);
}