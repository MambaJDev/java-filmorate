package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.userdao.UserDaoService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserDaoController {

    private final UserDaoService userDaoService;

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        return userDaoService.add(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userDaoService.update(user);
    }

    @DeleteMapping
    public User delete(@RequestBody User user) {
        return userDaoService.delete(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userDaoService.getAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userDaoService.addFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        return userDaoService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userDaoService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userDaoService.deleteFriend(id, friendId);
    }
}