package ru.yandex.practicum.filmorate.service.mpadao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaDaoServiceImpl implements MpaDaoService {

    private final MpaDao mpaDao;

    @Override
    public List<Mpa> getAllMpa() {
        log.info("Поступил GET-запрос на получение всех рейтингов mpa из базы данных");
        return mpaDao.getAllMpa();
    }

    @Override
    public Mpa getMpaById(Long id) {
        log.info("Поступил GET-запрос на получение рейтинга mpa по ID = {} из базы данных", id);
        checkMpaIsPresent(id);
        return mpaDao.getMpaById(id);
    }

    private void checkMpaIsPresent(Long id) {
        mpaDao.getAllMpa().stream()
                .filter(mpa -> mpa.getId().equals(id))
                .findFirst().orElseThrow(() -> new NotFoundException("Рейтинга с таким ID не существует"));
    }
}