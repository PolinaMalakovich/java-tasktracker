package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Manager {
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;

    public Manager(HashMap<Integer, Epic> epics, HashMap<Integer, Task> tasks, HashMap<Integer, Subtask> subtasks) {
        this.epics = epics;
        this.tasks = tasks;
        this.subtasks = subtasks;
    }

    public Set<Epic> getEpics() {
        return new HashSet<>(epics.values());
    }

    public Set<Task> getTasks() {
        return new HashSet<>(tasks.values());
    }

    public Set<Subtask> getSubtasks() {
        return new HashSet<>(subtasks.values());
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
        return epics.get(id);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    public void createTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic oldEpic = getEpicById(subtask.getEpicId());
            if (oldEpic != null) {
                subtasks.put(subtask.getId(), subtask);
                Set<Subtask> newSubtasks = new HashSet<>(getEpicSubtasks(oldEpic));
                newSubtasks.add(subtask);
                Epic epic = Epic.create(oldEpic.getTitle(), oldEpic.getDescription(), newSubtasks, oldEpic.getId());
                epics.replace(epic.getId(), epic);
            }
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null) {
            epics.replace(epic.getId(), epic);
        }
    }

    public void updateTask(Task task) {
        if (task != null) {
            tasks.replace(task.getId(), task);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask oldSubtask = getSubtaskById(subtask.getId());
            if (oldSubtask != null) {
                deleteSubtaskById(oldSubtask.getId());
                createSubtask(subtask);
            }
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            for (int i : epic.getSubtasks()) {
                deleteSubtaskById(i);
            }
            epics.remove(id);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask != null) {
            Epic oldEpic = getEpicById(subtask.getEpicId());
            if (oldEpic != null) {
                Set<Subtask> newSubtasks = new HashSet<>(getEpicSubtasks(oldEpic));
                newSubtasks.remove(subtask);
                Epic epic = Epic.create(oldEpic.getTitle(), oldEpic.getDescription(), newSubtasks, oldEpic.getId());
                updateEpic(epic);
                subtasks.remove(id);
            }
        }
    }

    public Set<Subtask> getEpicSubtasks(Epic epic) {
        Set<Subtask> epicSubtasks = new HashSet<>();

        if (epic != null) {
            for (int id : epic.getSubtasks()) {
                epicSubtasks.add(subtasks.get(id));
            }
        }

        return epicSubtasks;
    }
}
