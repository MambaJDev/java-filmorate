package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Override
    public void addLike(long filmId, long userId) {
        checkFilmIdIsPresent(filmId);
        userService.checkUserIdIsPresent(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (film.getUsersIdWhoLike().add(userId)) {
            film.setLike(film.getLike() + 1);
            log.info("User {} успешно поставил лайк фильму {}, количество лайков = {}", userId, filmId, film.getLike());
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        checkFilmIdIsPresent(filmId);
        userService.checkUserIdIsPresent(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (film.getUsersIdWhoLike().remove(userId)) {
            film.setLike(film.getLike() - 1);
            log.info("User успешно удалил лайк, количество лайков = {}", film.getLike());
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.getAll().stream()
                .sorted((film0, film1) -> film1.getLike() - film0.getLike())//// сделать сортировку
                .limit(count)
                .collect(Collectors.toList());
        log.info("Получен список из популярных фильмов, их количество {}", popularFilms.size());
        return popularFilms;
    }

    @Override
    public Film add(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film delete(Film film) {
        return filmStorage.delete(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public void checkFilmIdIsPresent(Long id) {
        if (filmStorage.getFilmById(id) == null) {
            log.info("Фильма с id = {} не найден", id);
            throw new NotFoundException("Фильма с таким ID не существует");
        }
    }
}