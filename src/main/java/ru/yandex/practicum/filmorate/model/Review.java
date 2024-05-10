package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    private Integer id;
    private String content;
    private Boolean isPositive;
    private Integer useful;
    private Integer userId;
    private Integer filmId;

}
