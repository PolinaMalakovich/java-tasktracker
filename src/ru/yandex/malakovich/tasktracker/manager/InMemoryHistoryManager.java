package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();

    @Override
    public void remove(Task task) {
        List<Task> taskList = new LinkedList<>();

        for (Task t : history) {
            if (t.getId() == task.getId()) {
                taskList.add(t);
            }
        }

        history.removeAll(taskList);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == 10) {
                history.remove(0);
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
