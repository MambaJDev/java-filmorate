package ru.yandex.practicum.filmorate.model.enums;

public enum Operation {

    ADD(1),
    UPDATE(2),
    REMOVE(3);

    private final int index;

    Operation(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
