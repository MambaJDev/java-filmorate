package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.genre.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreDBService implements GenreService {

    private final GenreDao genreDao;

    @Override
    public List<Genre> getAllGenres() {
        log.info("Поступил GET-запрос на получение всех жанров из базы данных");
       return genreDao.getAllGenres();
    }

    @Override
    public Genre getGenreById(Long id) {
        log.info("Поступил GET-запрос на получение жанра по ID = {} из базы данных", id);
        checkGenreIsPresent(id);
        return genreDao.getGenreById(id);
    }

    private void checkGenreIsPresent(Long id) {
        genreDao.getAllGenres().stream()
                .filter(genre -> genre.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException("Жанр с таким ID не существует"));
    }
}