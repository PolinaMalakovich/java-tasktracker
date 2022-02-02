package ru.yandex.malakovich.tasktracker;

public class IdGenerator {
    private int lastId;

    public IdGenerator(int lastId) {
        this.lastId = lastId;
    }

    public IdGenerator() {
        this(0);
    }

    public int getNewId() {
        int newId = lastId + 1;
        lastId = newId;
        return newId;
    }
}
