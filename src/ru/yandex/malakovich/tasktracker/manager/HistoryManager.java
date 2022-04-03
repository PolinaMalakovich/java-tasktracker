package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.List;

public interface HistoryManager {
    /**
     * Removes the first occurrence of the specified task from this list, if it is present.
     * @param task the element to be removed from history
     */
    void remove(Task task);

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
