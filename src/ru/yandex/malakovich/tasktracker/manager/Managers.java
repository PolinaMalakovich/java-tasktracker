package ru.yandex.malakovich.tasktracker.manager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
