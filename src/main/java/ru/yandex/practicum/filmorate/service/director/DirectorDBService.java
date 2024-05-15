package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.director.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorDBService implements DirectorService {

    private final DirectorDao directorDao;

    public List<Director> getDirectors() {
        return directorDao.getDirectors();
    }

    @Override
    public Director getDirector(int id) {
        return directorDao.getDirector(id);
    }

    @Override
    public Director addDirector(Director director) {
        return directorDao.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    @Override
    public void deleteDirector(int id) {
        directorDao.deleteDirector(id);
    }
}