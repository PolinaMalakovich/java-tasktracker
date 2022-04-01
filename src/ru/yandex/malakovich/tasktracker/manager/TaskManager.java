package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    /**
     * Returns a set of values from the epics map.
     * @return a set of values from the epics map
     */
    Set<Epic> getEpics();

    /**
     * Returns a set of values from the tasks map.
     * @return a set of values from the tasks map
     */
    Set<Task> getTasks();

    /**
     * Returns a set of values from the subtasks map.
     * @return a set of values from the subtasks map
     */
    Set<Subtask> getSubtasks();

    /**
     * Removes all the mappings from the epics map and the subtasks map.
     */
    void deleteAllEpics();

    /**
     * Removes all the mappings from the tasks map.
     */
    void deleteAllTasks();

    /**
     * Removes all the mappings from the subtasks map.
     */
    void deleteAllSubtasks();

    /**
     * Returns the epic to which the specified id is mapped and appends the epic to the end of the history list.
     * @param id the id mapped to an epic
     * @return the epic to which the specified id is mapped
     */
    Epic getEpicById(int id);

    /**
     * Returns the task to which the specified id is mapped and appends the task to the end of the history list.
     * @param id the id mapped to a task
     * @return the task to which the specified id is mapped
     */
    Task getTaskById(int id);

    /**
     * Returns the subtask to which the specified id is mapped and appends the subtask to the end of the history list.
     * @param id the id mapped to a subtask
     * @return the subtask to which the specified id is mapped
     */
    Subtask getSubtaskById(int id);

    /**
     * Associates the epic with its id in the epics map if the epic is not null.
     * @param epic an Epic object to be put into the epics map
     */
    void createEpic(Epic epic);

    /**
     * Associates the task with its id in the tasks map if the task is not null.
     * @param task a Task object to be put into the tasks map
     */
    void createTask(Task task);

    /**
     * Associates the subtask with its id in the subtasks map if the subtask is not null.
     * @param subtask a Subtask object to be put into the subtasks map
     */
    void createSubtask(Subtask subtask);

    /**
     * Replaces the epics map entry for the specified epic only if the epic is not null.
     * @param epic a non-null Epic object to replace another epic with the same id in the epics map
     */
    void updateEpic(Epic epic);

    /**
     * Replaces the tasks map entry for the specified task only if the task is not null.
     * @param task a non-null Task object to replace another task with the same id in the tasks map
     */
    void updateTask(Task task);

    /**
     * Replaces the subtasks map entry for the specified subtask only if the subtask is not null.
     * @param subtask a non-null subtask to replace another subtask with the same id in the subtasks map
     */
    void updateSubtask(Subtask subtask);

    /**
     * Removes the mapping for the specified id from the epics map if present.
     * @param id key whose mapping is to be removed from the epics map
     */
    void deleteEpicById(int id);

    /**
     * Removes the mapping for the specified id from the tasks map if present.
     * @param id key whose mapping is to be removed from the tasks map
     */
    void deleteTaskById(int id);

    /**
     * Removes the mapping for the specified id from the subtasks map if present.
     * @param id key whose mapping is to be removed from the subtasks map
     */
    void deleteSubtaskById(int id);

    /**
     * Returns a set of subtasks associated with the specified epic
     * @param epic Epic object whose associated subtasks are to be returned
     * @return a set of subtasks associated with the specified epic
     */
    Set<Subtask> getEpicSubtasks(Epic epic);

    /**
     * Returns a list of ten Task objects that were recently viewed.
     * @return a list of ten Task objects that were recently viewed
     */
    List<Task> history();
}
