package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Feed {
    private Long eventId;
    @NotNull
    private Long timestamp;
    @NotNull
    private Long userId;
    private EventType eventType;
    private Operation operation;
    @NotNull
    private Long entityId;
}
