package ru.yandex.malakovich.tasktracker.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.malakovich.tasktracker.KVTaskClient;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient kvClient;

    public HttpTaskManager(String host) {
        this(host, false);
    }

    public HttpTaskManager(String host, boolean needLoad) {
        super(null);
        kvClient = new KVTaskClient(host);
        if (needLoad) load();
    }

    public void load() {
        String tasksList = kvClient.load("tasks");
        String subtasksList = kvClient.load("subtasks");
        String epicsList = kvClient.load("epics");
        String historyList = kvClient.load("history");
        Gson gson = new Gson();
        List<Task> tasks = gson.fromJson(tasksList, new TypeToken<ArrayList<Task>>(){}.getType());
        List<Subtask> subtasks = gson.fromJson(subtasksList, new TypeToken<ArrayList<Subtask>>(){}.getType());
        List<Epic> epics = gson.fromJson(epicsList, new TypeToken<ArrayList<Epic>>(){}.getType());
        List<Task> history = gson.fromJson(historyList, new TypeToken<ArrayList<Task>>(){}.getType());

        tasks.forEach(task -> this.tasks.put(task.getId(), task));
        subtasks.forEach(subtask -> this.subtasks.put(subtask.getId(), subtask));
        epics.forEach(epic -> this.epics.put(epic.getId(), epic));
        history.forEach(this.historyManager::add);
    }

    @Override
    public void save() {
        List<Task> tasks = getTasks();
        List<Subtask> subtasks = getSubtasks();
        List<Epic> epics = getEpics();
        List<Task> history = history();
        Gson gson = new Gson();
        String tasksList = gson.toJson(tasks);
        String subtasksList = gson.toJson(subtasks);
        String epicsList = gson.toJson(epics);
        String historyList = gson.toJson(history);
        kvClient.put("tasks", tasksList);
        kvClient.put("subtasks", subtasksList);
        kvClient.put("epics", epicsList);
        kvClient.put("history", historyList);
    }
}
