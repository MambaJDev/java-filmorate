package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Qualifier("userDbService")
public class UserDbService implements UserService {

    private final UserDao userDao;

    @Override
    public User add(User user) {
        log.info("Поступил POST-запрос на добавление юзера с именем = {} в базу данных", user.getName());
        return userDao.add(user);
    }

    @Override
    public void deleteAllUsers() {
        userDao.deleteAllUsers();
    }

    @Override
    public void deleteUserById(Integer id) {
        if (getUserById(Long.valueOf(id)) == null) {
            log.info("Пользователь " + id + " не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        userDao.deleteUserById(id);
    }

    @Override
    public User update(User user) {
        log.info("Поступил PUT-запрос на обновление юзера с ID = {} в базе данных", user.getId());
        checkUserIdIsPresent(user.getId());
        return userDao.update(user);
    }

    @Override
    public User getUserById(Long id) {
        log.info("Поступил GET-запрос на получение юзера с ID = {} из базы данных", id);
        checkUserIdIsPresent(id);
        return userDao.getUserById(id);
    }

    @Override
    public List<User> getAll() {
        log.info("Поступил GET-запрос запрос на получение всех юзеров из базы данных");
        return userDao.getAll();
    }

    @Override
    public User addFriend(Long userID, Long friendID) {
        log.info("Поступил PUT-запрос на добавление друга {} юзеру {}", friendID, userID);
        checkUserIdIsPresent(userID);
        checkUserIdIsPresent(friendID);
        return userDao.addFriend(userID, friendID);
    }

    @Override
    public void deleteFriend(Long userID, Long friendID) {
        log.info("Поступил DELETE-запрос на удаление друга {} у юзера {}", friendID, userID);
        checkUserIdIsPresent(userID);
        checkUserIdIsPresent(friendID);
        userDao.deleteFriend(userID, friendID);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        log.info("Поступил GET-запрос на получение всех друзей пользователя с ID = {}", id);
        checkUserIdIsPresent(id);
        return userDao.getAllFriends(id);
    }

    @Override
    public List<User> getCommonFriends(Long userID, Long otherID) {
        log.info("Поступил GET-запрос на получение всех общих друзей юзеров с ID {} и {} ", userID, otherID);
        checkUserIdIsPresent(userID);
        checkUserIdIsPresent(otherID);
        return userDao.getCommonFriends(userID, otherID);
    }

    @Override
    public void checkUserIdIsPresent(Long id) {
        userDao.getAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException("Пользователь с таким ID не существует"));
    }
}