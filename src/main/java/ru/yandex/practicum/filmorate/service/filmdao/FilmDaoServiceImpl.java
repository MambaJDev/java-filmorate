package ru.yandex.practicum.filmorate.service.filmdao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.userdao.UserDaoService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmDaoServiceImpl implements FilmDaoService {

    private final FilmDao filmDao;
    private final UserDaoService userDaoService;

    @Override
    public Film add(Film film) {
        log.info("Поступил POST-запрос на добавление фильма с именем = {} в базу данных", film.getName());
        return filmDao.add(film);
    }

    @Override
    public Film update(Film film) {
        log.info("Поступил PUT-запрос на обновление фильма с ID = {} в базе данных", film.getId());
        checkFilmIsPresent(film.getId());
        return filmDao.update(film);
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Поступил GET-запрос на получение фильма с ID = {} из базы данных", id);
        checkFilmIsPresent(id);
        return filmDao.getFilmById(id);
    }

    @Override
    public List<Film> getAll() {
        log.info("Поступил GET-запрос запрос на получение всех фильмов из базы данных");
        return filmDao.getAll();
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Поступил GET-запрос на получение списка популярных фильмов из базы данных, размер списка = {}", count);
        return filmDao.getPopularFilms(count);
    }

    @Override
    public void addLike(Long filmID, Long userID) {
        log.info("Поступил PUT-запрос на добавление лайка фильму {} юзером {}", filmID, userID);
        checkFilmIsPresent(filmID);
        userDaoService.checkUserIsPresent(userID);
        filmDao.addLike(filmID, userID);
    }

    @Override
    public void deleteLike(Long filmID, Long userID) {
        log.info("Поступил DELETE-запрос на удаление лайка у фильма {} юзером {}", filmID, userID);
        checkFilmIsPresent(filmID);
        userDaoService.checkUserIsPresent(userID);
        filmDao.deleteLike(filmID, userID);
    }

    private void checkFilmIsPresent(Long id) {
        filmDao.getAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException("Фильм с таким ID не существует"));
    }
}