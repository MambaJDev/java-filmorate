package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("inMemoryUserService")
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

    @Override
    public User addFriend(Long userId, Long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        return userStorage.getAllFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public void deleteUserById(Integer id) {
        userStorage.deleteUserById(id);
    }

    @Override
    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public List<Feed> getFeedHistory(Long id) {
        return null;
    }
}