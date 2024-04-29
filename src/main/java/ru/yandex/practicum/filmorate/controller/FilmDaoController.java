package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.filmdao.FilmDaoService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmDaoController {

    private final FilmDaoService filmDaoService;
    private static final String DEFAULT_COUNT = "10";

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        return filmDaoService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmDaoService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmDaoService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmDaoService.getAll();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = DEFAULT_COUNT) Integer count) {
        return filmDaoService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Long filmID,
                        @PathVariable(value = "userId") Long userID) {
        filmDaoService.addLike(filmID, userID);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Long filmID,
                           @PathVariable(value = "userId") Long userID) {
        filmDaoService.deleteLike(filmID, userID);
    }
}