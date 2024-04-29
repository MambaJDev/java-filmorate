package ru.yandex.practicum.filmorate.dao.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MpaDaoImplTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testGetAllMpa() {
        final List<Mpa> newList = List.of(
                new Mpa(1L, "G"),
                new Mpa(2L, "PG"),
                new Mpa(3L, "PG-13"),
                new Mpa(4L, "R"),
                new Mpa(5L, "NC-17"));

        final MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        final List<Mpa> savedList = mpaDao.getAllMpa();

        assertThat(savedList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newList);
    }

    @Test
    void testGetMpaById() {
        final Mpa newMpa = new Mpa(1L, "G");
        final MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        final Mpa savedMpa = mpaDao.getMpaById(1L);

        assertThat(savedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newMpa);
    }
}