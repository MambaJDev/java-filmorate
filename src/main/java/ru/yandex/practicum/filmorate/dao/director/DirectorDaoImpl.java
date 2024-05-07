package ru.yandex.practicum.filmorate.dao.director;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
@Slf4j
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("select * from directors", directorRowMapper());
    }

    @Override
    public Director getDirector(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from directors where id=?", directorRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Режиссер с id = {} не найден", id);
            throw new NotFoundException("Режиссер с id = " + id + " не найден");
        }
    }

    @Override
    public Director addDirector(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");

        director.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        log.info("Добавлен режиссер {}", director);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        getDirector(director.getId());
        jdbcTemplate.update("update users set name = ? where id = ?",
                director.getName(),
                director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        getDirector(id);
        jdbcTemplate.update("delete from directors where id = ?", id);
        log.info("Режиссер с id={} удалён", id);
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
