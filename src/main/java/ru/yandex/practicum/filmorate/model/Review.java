package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


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
    @Min(0)
    private Integer userId;
    @NotNull
    @Min(0)
    private Integer filmId;

}
