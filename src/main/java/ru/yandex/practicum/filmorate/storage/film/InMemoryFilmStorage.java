package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private long idGenerator = 1;

    @Override
    public Film add(Film film) {
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен. Общее количество фильмов: {} ", films.size());
        return film;
    }

    @Override
    public Film delete(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
            log.info("Фильм успешно удален");
            return film;
        } else {
            log.info("Фильм не найден");
            throw new NotFoundException("Такого фильма нет");
        }
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
    public Film getFilmById(long id) {
        return films.get(id);
    }
}