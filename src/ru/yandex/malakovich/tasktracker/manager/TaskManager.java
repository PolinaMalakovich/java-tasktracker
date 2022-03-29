package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.Set;

public interface TaskManager {

    Set<Epic> getEpics();

    Set<Task> getTasks();

    Set<Subtask> getSubtasks();

    void deleteAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    Epic getEpicById(int id);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    void createEpic(Epic epic);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void deleteEpicById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    Set<Subtask> getEpicSubtasks(Epic epic);
}
