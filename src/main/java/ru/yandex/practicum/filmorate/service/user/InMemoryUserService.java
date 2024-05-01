package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(friendId);
        return userStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        checkUserIdIsPresent(id);
        return userStorage.getAllFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    @Override
    public User add(User user) {
        checkUserIdIsPresent(user.getId());
        return userStorage.add(user);
    }

    @Override
    public User delete(User user) {
        checkUserIdIsPresent(user.getId());
        return userStorage.delete(user);
    }

    @Override
    public User update(User user) {
        checkUserIdIsPresent(user.getId());
        return userStorage.update(user);
    }

    @Override
    public User getUserById(Long id) {
        checkUserIdIsPresent(id);
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public void checkUserIdIsPresent(Long id) {
        if (userStorage.getUserById(id) == null) {
            log.info("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с таким ID не существует");
        }
    }
}