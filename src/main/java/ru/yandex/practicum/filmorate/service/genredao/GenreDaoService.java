package ru.yandex.practicum.filmorate.service.genredao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDaoService {

    List<Genre> getAllGenres();

    Genre getGenreById(Long id);
}