package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getDirectors();

    Director getDirector(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
