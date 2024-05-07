package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.NotReleaseDateBefore1895;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotReleaseDateBefore1895
    private String releaseDate;
    @Min(1)
    private Long duration;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Long> userIdLikes = new HashSet<>();
    private int likes = 0;
    private Set<Director> directors = new HashSet<>();
}