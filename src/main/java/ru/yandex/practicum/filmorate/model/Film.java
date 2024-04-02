package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.annotation.NotReleaseDateBefore1895;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotReleaseDateBefore1895
    private String releaseDate;
    @Min(1)
    private long duration;
    private final Set<Long> usersIdWhoLike = new HashSet<>();
    @Min(0)
    private int like;
}