package ru.yandex.practicum.filmorate.dao.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DirectorDaoImplTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void getDirectors() {
        DirectorDao directorDao = new DirectorDaoImpl(jdbcTemplate);
        Director director1 = new Director(1, "Director1");
        directorDao.addDirector(director1);
        Director director2 = new Director(2, "Director2");
        directorDao.addDirector(director2);

        assertThat(directorDao.getDirectors())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(director1, director2));
    }

    @Test
    void addAndGetDirector() {
        DirectorDao directorDao = new DirectorDaoImpl(jdbcTemplate);
        Director director1 = new Director(1, "Director1");
        directorDao.addDirector(director1);

        assertThat(directorDao.getDirector(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director1);
    }

    @Test
    void updateDirector() {
        DirectorDao directorDao = new DirectorDaoImpl(jdbcTemplate);
        Director director1 = new Director(1, "Director1");
        directorDao.addDirector(director1);
        Director directorUpdated = new Director(1, "update");
        directorDao.updateDirector(directorUpdated);

        assertThat(directorDao.getDirector(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(directorUpdated);
    }

    @Test
    void deleteDirector() {
        DirectorDao directorDao = new DirectorDaoImpl(jdbcTemplate);
        Director director1 = new Director(1, "Director1");
        directorDao.addDirector(director1);
        Director director2 = new Director(2, "Director2");
        directorDao.addDirector(director2);

        directorDao.deleteDirector(1);

        assertThat(directorDao.getDirectors())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(director2));
    }
}