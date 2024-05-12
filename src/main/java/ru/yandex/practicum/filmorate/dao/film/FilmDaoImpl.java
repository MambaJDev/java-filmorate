package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    @Override
    public Film add(Film film) {
        String sqlQuery = "insert into films(name, description, release_date, duration) values (?, ?, ?, ?)";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()) == 0) {
            log.info("Операция обновления данных фильма в БД закончилась неудачей");
        }
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("select * from films where name = ?", film.getName());
        while (filmRow.next()) {
            film.setId(filmRow.getLong("id"));
            log.info("Фильму присвоен ID = {}", film.getId());
        }
        setFilmMpa(film);
        setFilmGenres(film);
        try {
            setFilmDirectors(film);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер не найден");
        }

        log.info("Фильм с ID = {} полностью добавился имя = {}, рейтинг = {}, список жанров = {}, режиссеры = {}",
                film.getId(), film.getName(), film.getMpa() == null ? "null" : film.getMpa().getName(), getAllGenresOfFilmToString(film), film.getDirectors());
        return film;
    }

    @Override
    public void deleteAllFilms() {
        try {
            String sql = "delete from films";
            jdbcTemplate.update(sql);
            String sql2 = "delete from films_genres";
            jdbcTemplate.update(sql2);
            String sql4 = "delete from films_users";
            jdbcTemplate.update(sql4);
        } catch (Exception e) {
            log.error("Ошибка в удалении фильма");
            throw new NotFoundException("Ошибка в удалении фильма");
        }
    }

    @Override
    public void deleteFilmById(Integer id) {
        try {
            String sql = "delete from films where id = ?";
            jdbcTemplate.update(sql, id);
            String sql2 = "delete from films_genres where film_id = ?";
            jdbcTemplate.update(sql2, id);
            String sql4 = "delete from films_users where film_id = ?";
            jdbcTemplate.update(sql4, id);
        } catch (Exception e) {
            log.error("Ошибка в удалении фильма по идентификатору");
            throw new NotFoundException("Ошибка в удалении фильма по идентификатору");
        }
    }

    @Override
    public Film update(Film film) {
        getFilmById(film.getId());
        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ? where id = ?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()) == 0) {
            log.info("Операция обновления данных фильма в БД закончилась неудачей");
        }
        jdbcTemplate.update("delete from film_director where film_id=?", film.getId());
        jdbcTemplate.update("delete from films_genres where film_id=?", film.getId());
        setFilmMpa(film);
        setFilmGenres(film);
        setFilmDirectors(film);
        log.info("Фильм с ID = {} полностью обновился имя = {}, рейтинг = {}, список жанров = {},  режиссеры = {}",
                film.getId(), film.getName(), film.getMpa() == null ? "null" : film.getMpa().getName(), getAllGenresOfFilmToString(film), film.getDirectors());
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            return jdbcTemplate.queryForObject("select * from films where id = ?", filmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query("select * from films", filmRowMapper());
        log.info("Получен список фильмов из БД, размер списка = {}", films.size());
        return films;
    }

    @Override
    public List<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Film> films;
        String sql = "select films.id, films.name, films.description, films.release_date, films.duration, films.mpa_id, films.likes from films";
        if (genreId == null && year == null) {
            sql = sql + " order by likes desc limit ?";
            films = jdbcTemplate.query(sql, filmRowMapper(), count);
            log.info("Получен список популярных фильмов. Количество популярных фильмов = {}", films.size());
        } else if (genreId != null && year == null) {
            sql = sql + " join films_genres fg on fg.film_id=films.id where fg.genre_id=? order by likes desc limit ?";
            films = jdbcTemplate.query(sql, filmRowMapper(), genreId, count);
            log.info("Получен список популярных фильмов. Количество популярных фильмов = {}", films.size());
        } else if (genreId == null && year != null) {
            String yearStart = year + "-01-01";
            String yearEnd = year + "-12-31";
            sql = sql + " where release_date between ? and ? order by likes desc limit ?";
            films = jdbcTemplate.query(sql, filmRowMapper(), yearStart, yearEnd, count);
            log.info("Получен список популярных фильмов. Количество популярных фильмов = {}", films.size());
        } else {
            String yearStart = year + "-01-01";
            String yearEnd = year + "-12-31";
            sql = sql + " join films_genres fg on fg.film_id=films.id " +
                    "where (fg.genre_id=? and (release_date between ? and ?)) order by likes desc limit ?";
            films = jdbcTemplate.query(sql, filmRowMapper(), genreId, yearStart, yearEnd, count);
            log.info("Получен список популярных фильмов. Количество популярных фильмов = {}", films.size());
        }

        return films;
    }

    @Override
    public void addLike(Long filmID, Long userID) {
        Film film = getFilmById(filmID);
        userDao.getUserById(userID);
        userDao.createFeedHistory(userID, EventType.LIKE, Operation.ADD, filmID);
        if (film.getUserIdLikes().contains(userID)) return;
        if (jdbcTemplate.update("insert into films_users(film_id, user_id) values (?, ?)", filmID, userID) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        film.getUserIdLikes().add(userID);
        film.setLikes(film.getLikes() + 1);
        setLikeIntoDataBase(filmID, film.getLikes());
    }

    @Override
    public void deleteLike(Long filmID, Long userID) {
        getFilmById(filmID);
        userDao.getUserById(userID);
        userDao.createFeedHistory(userID, EventType.LIKE, Operation.REMOVE, filmID);
        if (jdbcTemplate.update("delete from films_users where film_id = ? and user_id = ?", filmID, userID) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        Film film = getFilmById(filmID);
        film.getUserIdLikes().remove(userID);
        if (film.getLikes() >= 1) {
            film.setLikes(film.getLikes() - 1);
        }
        setLikeIntoDataBase(filmID, film.getLikes());
    }

    @Override
    public List<Film> getFilmsByDirector(String sortBy, int directorId) {
        try {
            jdbcTemplate.queryForObject("select * from directors where id=?", (rs, rowNum) -> new Director(
                    rs.getInt("id"),
                    rs.getString("name")
            ), directorId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер не найден");
        }
        if (sortBy.equals("year")) {
            sortBy = "release_date";
        }
        String sql = "select films.id, films.name, films.description, films.release_date, films.duration, films.mpa_id, films.likes " +
                "from directors d join film_director fd on d.id=fd.director_id join films on fd.film_id=films.id  where d.id=?" +
                " order by " + sortBy;
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper(), directorId);
        log.info("Получен список фильмов режиссера");
        return films;
    }

    @Override
    public List<Film> getFilmsByParams(String query, String by) {
        log.info("Получен список фильмов по параметрам");
        String sqlQueryByTitle = "select * from films where name ilike ? order by likes desc";
        String sqlQueryByDirector = "select * from films where id in (select film_id from film_director where director_id in " +
                "(select id from directors where name ilike ?)) order by likes desc";
        if (by.equals("director")) {
            return jdbcTemplate.query(sqlQueryByDirector, filmRowMapper(), "%" + query + "%");
        } else if (by.equals("title")) {
            return jdbcTemplate.query(sqlQueryByTitle, filmRowMapper(), "%" + query + "%");
        } else if (by.equals("director,title") || by.equals("title,director")) {
            List<Film> filmsByTitle = jdbcTemplate.query(sqlQueryByTitle, filmRowMapper(), "%" + query + "%");
            List<Film> filmsByDirector = jdbcTemplate.query(sqlQueryByDirector, filmRowMapper(), "%" + query + "%");
            Set<Film> unionSet = new HashSet<>();
            unionSet.addAll(filmsByTitle);
            unionSet.addAll(filmsByDirector);
            return unionSet.stream().collect(Collectors.toList());
        } else return Collections.emptyList();
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userDao.getUserById(userId);
        userDao.getUserById(friendId);
        return jdbcTemplate.query("select films.id, films.name, films.description, films.release_date, " +
                        "films.duration, films.mpa_id, films.likes from films " +
                        "join (SELECT film_id FROM films_users WHERE user_id = ? OR user_id = ? " +
                        "GROUP BY film_id HAVING COUNT(DISTINCT user_id) = 2) as common on common.film_id=films.id",
                filmRowMapper(), userId, friendId);
    }

    private void setLikeIntoDataBase(Long filmID, int likeAmount) {
        if (jdbcTemplate.update("update films set likes = ? where id = ?", likeAmount, filmID) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        log.info("Лайк успешно обновлен. Фильм с ID = {} количество лайков {}", filmID, likeAmount);
    }

    private void setFilmLikes(Film film) {
        List<Long> likesList = jdbcTemplate.query("select user_id from films_users where film_id = ?",
                (rs, rowNum) -> rs.getLong("user_id"), film.getId());
        film.setUserIdLikes(new HashSet<>(likesList));
    }

    private RowMapper<Film> filmRowMapper() {
        return ((rs, rowNum) -> {
            Film film = new Film()
                    .setId(rs.getLong("id"))
                    .setName(rs.getString("name"))
                    .setDescription(rs.getString("description"))
                    .setReleaseDate(rs.getString("release_date"))
                    .setDuration(rs.getLong("duration"))
                    .setLikes(rs.getInt("likes"));

            if (checkMpaIsPresent(rs.getLong("mpa_id"))) {
                film.setMpa(getMpaById(rs.getLong("mpa_id")));
            }
            getGenresByFilmId(rs.getLong("id"));
            setFilmLikes(film);
            film.setGenres(getGenresByFilmId(rs.getLong("id")));
            log.info(" В фильм с ID = {} записался список жанров ", film.getId());
            film.setDirectors(getDirectorsByFilmId(film.getId()));
            log.info(" В фильм с ID = {} записался список режиссеров ", film.getId());
            return film;
        });
    }

    private boolean checkMpaIsPresent(Long id) {
        return getAllMpa().stream().anyMatch(mpa -> mpa.getId().equals(id));
    }

    private List<Genre> getGenresByFilmId(Long id) {
        return new ArrayList<>(jdbcTemplate.query("select g.id as genre_id, g.name as genre_name from genres as g " +
                        "join films_genres as fg on g.id = fg.genre_id where fg.film_id = ? order by genre_id",
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")), id));
    }

    private Set<Director> getDirectorsByFilmId(Long id) {
        return new HashSet<>(jdbcTemplate.query("select d.id as director_id, d.name as director_name from directors as d " +
                        "join film_director as fd on d.id = fd.director_id where fd.film_id = ? order by director_id",
                (rs, rowNum) -> new Director(rs.getInt("director_id"), rs.getString("director_name")), id));
    }

    private void setFilmMpa(Film film) {
        Long mpaId = film.getMpa().getId();
        String mpaName = getMpaById(mpaId).getName();
        film.setMpa(new Mpa(mpaId, mpaName));
        jdbcTemplate.update("update films set mpa_id = ? where id = ?", mpaId, film.getId());
        log.info("Фильму с ID = {} присвоился рейтинг {}", film.getId(), mpaName);
    }

    private void setFilmGenres(Film film) {
        film.setGenres(film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList()));
        film.getGenres().forEach(genre -> {
            genre.setName(getGenreById(genre.getId()).getName());
            jdbcTemplate.update("insert into films_genres(film_id, genre_id) values (?, ?)",
                    film.getId(), genre.getId());
        });
        log.info("Фильму с ID = {} записались жанры = {}", film.getId(), getAllGenresOfFilmToString(film));
    }

    private void setFilmDirectors(Film film) {

        Set<Director> directors = film.getDirectors();
        directors.forEach(director -> {
            director.setName(jdbcTemplate.queryForObject("select name from directors where id = ?", (rs, rowNum) ->
                    rs.getString("name"), director.getId()
            ));

            jdbcTemplate.update("insert into film_director(film_id, director_id) values (?, ?)",
                    film.getId(), director.getId());
        });
        film.setDirectors(directors);
        log.info("Фильму с ID = {} записались режиссеры = {}", film.getId(), film.getDirectors());
    }

    private String getAllGenresOfFilmToString(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            film.getGenres().forEach(genre -> builder.append(genre.getName()).append(", "));
            return builder.substring(0, builder.length() - 2);
        }
        return "null";
    }

    private Mpa getMpaById(Long id) {
        return jdbcTemplate.queryForObject("select * from mpa_ratings where id = ?", (rs, rowNum) ->
                new Mpa(rs.getLong("id"), rs.getString("name")), id);
    }

    private List<Mpa> getAllMpa() {
        return jdbcTemplate.query("select * from mpa_ratings", (rs, rowNum) ->
                new Mpa(rs.getLong("id"), rs.getString("name")));
    }

    private Genre getGenreById(Long id) {
        return jdbcTemplate.queryForObject("select * from genres where id = ?", (rs, rowNum) ->
                new Genre(rs.getLong("id"), rs.getString("name")), id);
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        String sqlGetFilmsIdByUserId = "select film_id from films_users where user_id=?";
        List<Long> idFilmsOfOurUser = jdbcTemplate.query(sqlGetFilmsIdByUserId, (rs, rowNum) -> rs.getLong("film_id"), id);
        if (idFilmsOfOurUser.isEmpty()) {
            log.info("Не найдено фильмов для пользователя с идентификатором {}", id);
            return Collections.emptyList();
        }
        String sqlAllUsersIdWhoLikedSameFilms = "select distinct user_id from films_users where film_id in " +
                "(" + sqlGetFilmsIdByUserId + ") and user_id != ?";
        log.info("Поиск пользователей, которым понравились те же фильмы, что и пользователю с идентификатором {}", id);
        List<Long> usersId = jdbcTemplate.query(sqlAllUsersIdWhoLikedSameFilms, (rs, rowNum) -> rs.getLong("user_id"), id, id);
        if (usersId.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Film> data = new HashSet<>();
        for (Long userId : usersId) {
            List<Long> userFilmsId = jdbcTemplate.query(sqlGetFilmsIdByUserId, (rs, rowNum) -> rs.getLong("film_id"), userId);
            for (Long filmId : userFilmsId) {
                if (!idFilmsOfOurUser.contains(filmId)) {
                    data.add(getFilmById(filmId));
                }
            }
        }
        log.info("Рекомендации, сгенерированные для пользователя с идентификатором {}", id);
        return data.stream().collect(Collectors.toList());
    }
}