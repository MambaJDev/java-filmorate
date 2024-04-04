package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public User getUserById(long id) {
        return users.get(id);
    }
}