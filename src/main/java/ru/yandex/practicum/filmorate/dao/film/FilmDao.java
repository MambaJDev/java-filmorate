package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    Film add(Film film);

    void deleteFilmById(Integer id);

    void deleteAllFilms();

    Film update(Film film);

    Film getFilmById(Long id);

    List<Film> getAll();

    List<Film> getPopularFilms(int count);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getFilmsByDirector(String sortBy, int directorId);
}