package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@Data
public class Genre {
    private Long id;
    @NotBlank
    private String name;
}