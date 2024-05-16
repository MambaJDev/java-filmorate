package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    private Integer reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    private Integer useful;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
}
