package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Qualifier("inMemoryFilmService")
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    @Qualifier("inMemoryUserService")
    private final UserService userService;

    @Override
    public void addLike(Long filmId, Long userId) {
        checkFilmIdIsPresent(filmId);
        userService.checkUserIdIsPresent(userId);
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        checkFilmIdIsPresent(filmId);
        userService.checkUserIdIsPresent(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getFilmsByDirector(String sortBy, int directorId) {
        return null;
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film add(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film delete(Film film) {
        checkFilmIdIsPresent(film.getId());
        return filmStorage.delete(film);
    }

    @Override
    public Film update(Film film) {
        checkFilmIdIsPresent(film.getId());
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilmIdIsPresent(id);
        return filmStorage.getFilmById(id);
    }

    private void checkFilmIdIsPresent(Long id) {
        if (filmStorage.getFilmById(id) == null) {
            log.info("Фильма с id = {} не найден", id);
            throw new NotFoundException("Фильма с таким ID не существует");
        }
    }
}