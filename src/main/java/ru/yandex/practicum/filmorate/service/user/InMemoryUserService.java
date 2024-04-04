package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Override
    public void addFriend(Long userId, Long friendId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь c id = {} стал другом пользователю {}", friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(friendId);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь успешно удален из друзей");
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        checkUserIdIsPresent(userId);
        log.info("Получен список друзей Пользлвателя с id = {}", userId);
        return userStorage.getUserById(userId).getFriends().stream()
                .map(userStorage::getUserById)
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(otherId);
        log.info("Получен список общих друзей Пользлвателя с id = {} и Пользователя с id = {}", userId, otherId);
        return userStorage.getUserById(userId).getFriends().stream()
                .filter(id -> userStorage.getUserById(otherId).getFriends().contains(id))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User delete(User user) {
        return userStorage.delete(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
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