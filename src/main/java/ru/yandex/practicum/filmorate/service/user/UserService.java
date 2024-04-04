package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getAllFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherId);

    User add(User user);

    User delete(User user);

    User update(User user);

    List<User> getAll();

    void checkUserIdIsPresent(Long id);
}