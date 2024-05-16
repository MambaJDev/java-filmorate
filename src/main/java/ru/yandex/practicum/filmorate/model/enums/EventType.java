package ru.yandex.practicum.filmorate.model.enums;

public enum EventType {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);

    private final int index;

    EventType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
