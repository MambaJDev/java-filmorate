package ru.yandex.practicum.filmorate.dao.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {

    List<Mpa> getAllMpa();

    Mpa getMpaById(Long id);
}