package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.List;

public interface HistoryManager {
    /**
     * Appends the specified task to the end of the history list.
     * @param task Task object to be appended to the history list
     */
    void add(Task task);

    /**
     * Returns a list of ten Task objects that were recently viewed.
     * @return a list of ten Task objects that were recently viewed.
     */
    List<Task> getHistory();
}
