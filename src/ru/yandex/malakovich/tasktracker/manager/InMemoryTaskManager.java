package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;
import ru.yandex.malakovich.tasktracker.util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1;
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Epic> getEpics() { return new ArrayList<>(epics.values()); }

    @Override
    public List<Task> getTasks() { return new ArrayList<>(tasks.values()); }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public static int getId() {
        return id++;
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic);
        }
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);

            return epic;
        } else {
            System.out.println("Id not found");

            return null;
        }
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);

            return task;
        } else {
            System.out.println("Id not found");

            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);

            return subtask;
        } else {
            System.out.println("Id not found");

            return null;
        }

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
            historyManager.remove(epic);
            epics.remove(id);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(tasks.get(id));
            tasks.remove(id);
        } else {
            System.out.println("Id not found");
        }
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
                historyManager.remove(subtask);
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
