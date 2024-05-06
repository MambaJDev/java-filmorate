package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

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

        log.info("Фильм с ID = {} полностью добавился имя = {}, реитенг = {}, списк жаннров = {}",
                film.getId(), film.getName(), film.getMpa() == null ? "null" : film.getMpa().getName(), getAllGenresOfFilmToString(film));
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
        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ? where id = ?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()) == 0) {
            log.info("Операция обновления данных фильма в БД закончилась неудачей");
        }
        setFilmMpa(film);
        setFilmGenres(film);
        log.info("Фильм с ID = {} полностью обновился имя = {}, реитенг = {}, списк жаннров = {}",
                film.getId(), film.getName(), film.getMpa() == null ? "null" : film.getMpa().getName(), getAllGenresOfFilmToString(film));
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = jdbcTemplate.queryForObject("select * from films where id = ?", filmRowMapper(), id);
        log.info("Фильм успешно получен по ID = {}", id);
        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbcTemplate.query("select * from films", filmRowMapper());
        log.info("Получен список фильмов из БД, размер списка = {}", films.size());
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = jdbcTemplate.query("select * from films order by likes desc limit ?", filmRowMapper(), count);
        log.info("Получен список популярных фильмов. Количество популярных фильмов = {}", films.size());
        return films;
    }

    @Override
    public void addLike(Long filmID, Long userID) {
        if (jdbcTemplate.update("insert into films_users(film_id, user_id) values (?, ?)", filmID, userID) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        Film film = getFilmById(filmID);
        film.getUserIdLikes().add(userID);
        film.setLikes(film.getLikes() + 1);
        setLikeIntoDataBase(filmID, film.getLikes());
    }

    @Override
    public void deleteLike(Long filmID, Long userID) {
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

    private void setLikeIntoDataBase(Long filmID, int likeAmount) {
        if (jdbcTemplate.update("update films set likes = ? where id = ?", likeAmount, filmID) == 0) {
            log.info("Операция обновления данных в БД закончилась неудачей");
        }
        log.info("Лайк успешно обновлен. Фильм с ID = {} количество лайков {}", filmID, likeAmount);
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
            film.setGenres(getGenresByFilmId(rs.getLong("id")));
            log.info(" В фильм с ID = {} записался список жанров ", film.getId());
            return film;
        });
    }

    private boolean checkMpaIsPresent(Long id) {
        return getAllMpa().stream().anyMatch(mpa -> mpa.getId().equals(id));
    }

    private Set<Genre> getGenresByFilmId(Long id) {
        return new HashSet<>(jdbcTemplate.query("select g.id as genre_id, g.name as genre_name from genres as g " +
                        "join films_genres as fg on g.id = fg.genre_id where fg.film_id = ? order by genre_id",
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")), id));
    }

    private void setFilmMpa(Film film) {
        Long mpaId = film.getMpa().getId();
        String mpaName = getMpaById(mpaId).getName();
        film.setMpa(new Mpa(mpaId, mpaName));
        jdbcTemplate.update("update films set mpa_id = ? where id = ?", mpaId, film.getId());
        log.info("Фильму с ID = {} присвоился рейтинг {}", film.getId(), mpaName);
    }

    private void setFilmGenres(Film film) {
        Set<Genre> genres = film.getGenres();
        genres.forEach(genre -> {
            genre.setName(getGenreById(genre.getId()).getName());
            jdbcTemplate.update("insert into films_genres(film_id, genre_id) values (?, ?)",
                    film.getId(), genre.getId());
        });
        film.setGenres(genres);
        log.info("Фильму с ID = {} записались жанры = {}", film.getId(), getAllGenresOfFilmToString(film));
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
}