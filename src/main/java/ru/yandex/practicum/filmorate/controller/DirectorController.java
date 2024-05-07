package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.List;

@RestController
@Data
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService service;

    @GetMapping
    public List<Director> getDirectors() {
        return service.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable int id) {
        return service.getDirector(id);
    }

    @PostMapping
    public Director addDirector(@RequestBody Director director) {
        return service.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        service.deleteDirector(id);
    }

}
