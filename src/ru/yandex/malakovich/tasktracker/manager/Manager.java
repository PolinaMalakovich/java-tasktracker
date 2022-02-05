package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class Manager {
    public static final String EPIC_CANNOT_BE_NULL = "Epic cannot be null";
    public static final String TASK_CANNOT_BE_NULL = "Task cannot be null";
    public static final String SUBTASK_CANNOT_BE_NULL = "Subtask cannot be null";
    public static final String SUBTASK_DOES_NOT_EXIST = "Subtask does not exist";
    public static final String EPIC_DOES_NOT_EXIST = "Epic does not exist";
    public static final String TASK_DOES_NOT_EXIST = "Task does not exist";
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;

    public Manager(HashMap<Integer, Epic> epics, HashMap<Integer, Task> tasks, HashMap<Integer, Subtask> subtasks) {
        this.epics = epics;
        this.tasks = tasks;
        this.subtasks = subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return requireNonNull(epics.get(id), EPIC_DOES_NOT_EXIST);
    }

    public Task getTaskById(int id) {
        return requireNonNull(tasks.get(id), TASK_DOES_NOT_EXIST);
    }

    public Subtask getSubtaskById(int id) {
        return requireNonNull(subtasks.get(id), SUBTASK_DOES_NOT_EXIST);
    }

    public void createEpic(Epic epic) {
        requireNonNull(epic, EPIC_CANNOT_BE_NULL);
        epics.put(epic.getId(), epic);
    }

    public void createTask(Task task) {
        requireNonNull(task, TASK_CANNOT_BE_NULL);
        tasks.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask) {
        requireNonNull(subtask, SUBTASK_CANNOT_BE_NULL);
        Epic oldEpic = getEpicById(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        Set<Subtask> newSubtasks = new HashSet<>(getEpicSubtasks(oldEpic));
        newSubtasks.add(subtask);
        Epic epic = Epic.create(oldEpic.getTitle(), oldEpic.getDescription(), newSubtasks, oldEpic.getId());
        epics.replace(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        requireNonNull(epic, EPIC_CANNOT_BE_NULL);
        epics.replace(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        requireNonNull(task, TASK_CANNOT_BE_NULL);
        tasks.replace(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        requireNonNull(subtask, SUBTASK_CANNOT_BE_NULL);
        Subtask oldSubtask = getSubtaskById(subtask.getId());
        deleteSubtaskById(oldSubtask.getId());
        createSubtask(subtask);
    }

    public void deleteEpicById(int id) {
        for (int i : getEpicById(id).getSubtasks()) {
            deleteSubtaskById(i);
        }
        epics.remove(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        Epic oldEpic = getEpicById(subtask.getEpicId());
        Set<Subtask> newSubtasks = new HashSet<>(getEpicSubtasks(oldEpic));
        newSubtasks.remove(subtask);
        Epic epic = Epic.create(oldEpic.getTitle(), oldEpic.getDescription(), newSubtasks, oldEpic.getId());
        updateEpic(epic);
        subtasks.remove(id);
    }

    public Set<Subtask> getEpicSubtasks(Epic epic) {
        Set<Subtask> epicSubtasks = new HashSet<>();
        for (int id : epic.getSubtasks()) {
            epicSubtasks.add(subtasks.get(id));
        }

        return epicSubtasks;
    }
}
