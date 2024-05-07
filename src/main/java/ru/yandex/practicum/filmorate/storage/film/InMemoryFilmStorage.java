package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public Film add(Film film) {
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен. Общее количество фильмов: {} ", films.size());
        return film;
    }

    @Override
    public void deleteFilmById(Integer id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Фильм успешно удален");
        } else {
            log.info("Фильм не найден");
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
        log.info("Все фильмы удалены");
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм успешно обновлен");
            return film;
        } else {
            log.info("Фильм не найден");
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public List<Film> getAll() {
        log.info("Общее количество фильмов {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = getAll().stream()
                .sorted((film0, film1) -> film1.getLikes() - film0.getLikes())
                .limit(count)
                .collect(Collectors.toList());
        log.info("Получен список из популярных фильмов, их количество {}", popularFilms.size());
        return popularFilms;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (film.getUserIdLikes().add(userId)) {
            film.setLikes(film.getLikes() + 1);
            log.info("User {} успешно поставил лайк фильму {}, количество лайков = {}", userId, filmId, film.getLikes());
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        if (film.getUserIdLikes().remove(userId)) {
            film.setLikes(film.getLikes() - 1);
            log.info("User успешно удалил лайк, количество лайков = {}", film.getLikes());
        }
    }
}