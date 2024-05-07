package ru.yandex.practicum.filmorate.dao.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {
    List<Director> getDirectors();

    Director getDirector(int id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);


}
