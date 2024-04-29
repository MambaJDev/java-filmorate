package ru.yandex.practicum.filmorate.service.mpadao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDaoService {

    List<Mpa> getAllMpa();

    Mpa getMpaById(Long id);
}