package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно добавлен. Общее количество пользователей: {}", user.getId(), users.size());
        return user;
    }

    @Override
    public User delete(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            log.info("Пользователь c id = {} успешно удален", user.getId());
            return user;
        } else {
            log.info("Пользователь не найден");
            throw new NotFoundException("Такого пользователя нет");
        }
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь c id = {} успешно обновлен", user.getId());
            return user;
        } else {
            log.info("Пользователь не найден");
            throw new NotFoundException("Такого пользователя нет");
        }
    }

    @Override
    public List<User> getAll() {
        log.info("Получен список пользователей, Общее количество пользователей {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    public User addFriend(Long userId, Long friendId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь c id = {} стал другом пользователю {}", friendId, userId);
        return user;
    }


    public void checkUserIdIsPresent(Long id) {
        if (getUserById(id) == null) {
            log.info("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с таким ID не существует");
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        checkUserIdIsPresent(id);
        log.info("Получен список друзей Пользлвателя с id = {}", id);
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(otherId);
        log.info("Получен список общих друзей Пользлвателя с id = {} и Пользователя с id = {}", userId, otherId);
        return getUserById(userId).getFriends().stream()
                .filter(id -> getUserById(otherId).getFriends().contains(id))
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        checkUserIdIsPresent(userId);
        checkUserIdIsPresent(friendId);
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь успешно удален из друзей");
    }
}