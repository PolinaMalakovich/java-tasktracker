package ru.yandex.malakovich.tasktracker.util;

import ru.yandex.malakovich.tasktracker.manager.HistoryManager;
import ru.yandex.malakovich.tasktracker.manager.InMemoryHistoryManager;
import ru.yandex.malakovich.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.malakovich.tasktracker.manager.TaskManager;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
