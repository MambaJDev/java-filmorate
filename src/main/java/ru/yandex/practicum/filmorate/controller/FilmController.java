package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    @Qualifier("filmDbService")
    private final FilmService filmService;
    private static final String DEFAULT_COUNT = "10";

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = DEFAULT_COUNT) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Long filmID,
                        @PathVariable(value = "userId") Long userID) {
        filmService.addLike(filmID, userID);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Long filmID,
                           @PathVariable(value = "userId") Long userID) {
        filmService.deleteLike(filmID, userID);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        filmService.deleteAllFilms();
    }

    @DeleteMapping("{id}")
    public void deleteFilmById(@PathVariable int id) {
        filmService.deleteFilmById(id);
    }
}