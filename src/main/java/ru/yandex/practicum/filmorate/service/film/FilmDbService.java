package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Qualifier("filmDbService")
public class FilmDbService implements FilmService {

    private final FilmDao filmDao;

    @Override
    public Film add(Film film) {
        log.info("Поступил POST-запрос на добавление фильма с именем = {} в базу данных", film.getName());
        return filmDao.add(film);
    }

    @Override
    public Film update(Film film) {
        log.info("Поступил PUT-запрос на обновление фильма с ID = {} в базе данных", film.getId());
        return filmDao.update(film);
    }

    @Override
    public void deleteAllFilms() {
        filmDao.deleteAllFilms();
    }

    @Override
    public void deleteFilmById(Integer id) {
        filmDao.deleteFilmById(id);
    }


    @Override
    public Film getFilmById(Long id) {
        log.info("Поступил GET-запрос на получение фильма с ID = {} из базы данных", id);
        return filmDao.getFilmById(id);
    }

    @Override
    public List<Film> getAll() {
        log.info("Поступил GET-запрос запрос на получение всех фильмов из базы данных");
        return filmDao.getAll();
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        log.info("Поступил GET-запрос на получение списка популярных фильмов из базы данных, размер списка = {}", count);
        return filmDao.getPopularFilms(count, genreId, year);
    }

    @Override
    public void addLike(Long filmID, Long userID) {
        log.info("Поступил PUT-запрос на добавление лайка фильму {} юзером {}", filmID, userID);
        filmDao.addLike(filmID, userID);
    }

    @Override
    public void deleteLike(Long filmID, Long userID) {
        log.info("Поступил DELETE-запрос на удаление лайка у фильма {} юзером {}", filmID, userID);
        filmDao.deleteLike(filmID, userID);
    }

    @Override
    public List<Film> getFilmsByDirector(String sortBy, int directorId) {
        log.info("Поступил GET-запрос на получение списка фильмов sortBy={}, directorId={}", sortBy, directorId);
        return filmDao.getFilmsByDirector(sortBy, directorId);
    }

    @Override
    public List<Film> getFilmsByParams(String query, String by) {
        if (!(by.equals("director") || by.equals("title") || by.equals("director,title") || by.equals("title,director"))) {
            throw new NotFoundException("Неправильно выбран параметр 'by'");
        }
        log.info("Поступил GET-запрос на получение списка фильмов по параметрам query={}, by={}", query, by);
        return filmDao.getFilmsByParams(query, by);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmDao.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        log.info("Поступил GET-запрос на получение рекомендаций {}", id);
        return filmDao.getRecommendations(id);
    }
}