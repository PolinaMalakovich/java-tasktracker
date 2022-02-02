package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Status;
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
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).getSubtasks().add(subtask.getId());
    }

    public void updateEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        subtasks.replace(subtask.getId(), subtask);

        if (oldSubtask != null) {
            if (oldSubtask.getEpicId() != subtask.getEpicId()) {
                Epic oldEpic = epics.get(oldSubtask.getEpicId());
                oldEpic.getSubtasks().remove(subtask.getId());
                Epic newEpic = epics.get(subtask.getEpicId());
                newEpic.getSubtasks().add(subtask.getId());
            }

            if (oldSubtask.getStatus() != subtask.getStatus()) {
                Epic e = epics.get(subtask.getEpicId());
                int statusNew = 0;
                int statusDone = 0;

                for (Subtask s : getEpicSubtasks(e)) {
                    switch (s.getStatus()) {
                        case NEW:
                            statusNew += 1;
                            break;
                        case DONE:
                            statusDone += 1;
                            break;
                    }
                }

                Status status;

                if (statusNew == e.getSubtasks().size()) {
                    status = Status.NEW;
                } else if (statusDone == e.getSubtasks().size()) {
                    status = Status.DONE;
                } else {
                    status = Status.IN_PROGRESS;
                }

                Epic epic = new Epic(e.getTitle(),
                        e.getDescription(),
                        status,
                        e.getSubtasks(),
                        e.getId());
                epics.replace(epic.getId(), epic);
            }
        }
    }

    public void deleteEpicById(Integer id) {
        epics.remove(id);
    }

    public void deleteTaskById(Integer id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(Integer id) {
        subtasks.remove(id);
    }

    public Set<Subtask> getEpicSubtasks(Epic epic) {
        Set<Subtask> epicSubtasks = new HashSet<>();
        for (Integer id : epic.getSubtasks()) {
            epicSubtasks.add(subtasks.get(id));
        }

        return epicSubtasks;
    }
}
