package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);

    Film add(Film film);

    Film delete(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getFilmById(Long id);
}