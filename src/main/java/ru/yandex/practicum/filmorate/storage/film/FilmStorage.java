package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    void deleteFilmById(Integer id);

    void deleteAllFilms();

    Film update(Film film);

    List<Film> getAll();

    Film getFilmById(Long id);

    List<Film> getPopularFilms(int count);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}