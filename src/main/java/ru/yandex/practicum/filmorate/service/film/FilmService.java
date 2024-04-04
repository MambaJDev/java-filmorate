package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);

    Film add(Film film);

    Film delete(Film film);

    Film update(Film film);

    List<Film> getAll();

    void checkFilmIdIsPresent(Long id);
}