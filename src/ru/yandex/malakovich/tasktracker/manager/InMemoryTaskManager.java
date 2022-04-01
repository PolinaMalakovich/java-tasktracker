package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;
import ru.yandex.malakovich.tasktracker.util.Managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager(HashMap<Integer, Epic> epics, HashMap<Integer, Task> tasks, HashMap<Integer,
            Subtask> subtasks) {
        this.epics = epics;
        this.tasks = tasks;
        this.subtasks = subtasks;
    }

    public InMemoryTaskManager() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Override
    public Set<Epic> getEpics() { return new HashSet<>(epics.values()); }

    @Override
    public Set<Task> getTasks() {
        return new HashSet<>(tasks.values());
    }

    @Override
    public Set<Subtask> getSubtasks() {
        return new HashSet<>(subtasks.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
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

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            epics.replace(epic.getId(), epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            tasks.replace(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Subtask oldSubtask = getSubtaskById(subtask.getId());
            if (oldSubtask != null) {
                deleteSubtaskById(oldSubtask.getId());
                createSubtask(subtask);
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = getEpicById(id);
        if (epic != null) {
            for (int i : epic.getSubtasks()) {
                deleteSubtaskById(i);
            }
            epics.remove(id);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public Set<Subtask> getEpicSubtasks(Epic epic) {
        Set<Subtask> epicSubtasks = new HashSet<>();

        if (epic != null) {
            for (int id : epic.getSubtasks()) {
                epicSubtasks.add(subtasks.get(id));
            }
        }

        return epicSubtasks;
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }
}
