package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film add(Film film);

    Film update(Film film);

    void deleteFilmById(Integer id);

    void deleteAllFilms();

    Film getFilmById(Long id);

    List<Film> getAll();

    List<Film> getPopularFilms(int count);

    void addLike(Long filmID, Long userID);

    void deleteLike(Long filmID, Long userID);
}