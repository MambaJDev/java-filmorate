package ru.yandex.practicum.filmorate.service.userdao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDaoService {

    User add(User user);

    User delete(User user);

    User update(User user);

    User getUserById(Long id);

    List<User> getAll();

    User addFriend(Long userID, Long friendID);

    void deleteFriend(Long userID, Long friendID);

    List<User> getAllFriends(Long id);

    List<User> getCommonFriends(Long userID, Long otherID);

    void checkUserIsPresent(Long id);
}