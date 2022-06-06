package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.exception.ManagerLoadException;
import ru.yandex.malakovich.tasktracker.exception.ManagerSaveException;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Status;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;
import ru.yandex.malakovich.tasktracker.model.Type;
import ru.yandex.malakovich.tasktracker.util.ArrayUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static ru.yandex.malakovich.tasktracker.model.Type.SUBTASK;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final String HEADER = "id,type,name,status,description,duration,start,epic";
    public static final int HEADER_INDEX = 0;
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();

        return epic;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();

        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();

        return subtask;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            Epic epic = epics.get(subtask.getEpicId());
            super.createSubtaskWorker(epic, subtask);
            save();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    private void save() {
        Path path = file.toPath();
        if (Files.isWritable(path)) {

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                List<Task> list = new ArrayList<>();
                list.addAll(getSubtasks());
                list.addAll(getTasks());
                list.addAll(getEpics());
                bufferedWriter.write(HEADER);
                bufferedWriter.newLine();

                for (Task task : list) {
                    bufferedWriter.write(taskToString(task));
                    bufferedWriter.newLine();
                }

                bufferedWriter.newLine();
                bufferedWriter.write(historyManagerToString(historyManager));
                bufferedWriter.newLine();
                bufferedWriter.flush();

            } catch (IOException exception) {
                throw new ManagerSaveException(
                        "Can't save to file: " + file.getAbsolutePath(), exception.getCause());
            }
        }
    }

    // Наставник Сергей Савельев сказал сделать этот метод public,
    // смотри ответы в этом треде https://yandex-students.slack.com/archives/C03392E7N69/p1652367547922429
    // также наставник сказал, что .idea и проектный .iml не нужны (см. тот же тред)
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Map<Integer, Task> allTasks = new HashMap<>();
        Map<Integer, List<Integer>> subtasks = new HashMap<>();

        List<String> list;
        try {
            list = Files.readAllLines(file.toPath());
        } catch (IOException exception) {
            throw new ManagerLoadException("Can't read form file: " + file.getName(), exception);
        }

        if (list.isEmpty()) {
            return manager;
        }

        if (!HEADER.equals(list.get(HEADER_INDEX))) {
            throw new ManagerLoadException("Header mismatch");
        }

        for (int i = 1; i < list.size(); i++) {
            String item = list.get(i);
            if (item.isBlank()) {
                break;
            }
            Task task = taskFromString(item);
            if (task != null) {
                allTasks.put(task.getId(), task);

                switch (task.getType()) {
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(task.getId(), subtask);
                        subtasks.putIfAbsent(subtask.getEpicId(), new ArrayList<>());
                        subtasks.get(subtask.getEpicId()).add(subtask.getId());
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        if (subtasks.containsKey(epic.getId()))
                            epic.getSubtasks().addAll(subtasks.get(epic.getId()));
                        manager.epics.put(task.getId(), epic);
                        break;
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    default:
                        System.out.println("Unsupported task type: " + task.getType());
                }
            }
        }

        if (list.size() > 1) {
            String history = list.get(list.size() - 1);
            List<Integer> ids = historyManagerFromString(history);

            for (Integer id : ids) {
                manager.historyManager.add(allTasks.get(id));
            }
        }

        return manager;
    }

    private static String historyManagerToString(HistoryManager manager) {
        String result = "";

        if (manager != null) {
            List<String> ids = new ArrayList<>();

            for (Task task : manager.getHistory()) {
                ids.add(String.valueOf(task.getId()));
            }

            result = String.join(",", ids);
        }

        return result;
    }

    private static List<Integer> historyManagerFromString(String value) {
        List<Integer> list = new ArrayList<>();

        if (value != null && !value.isBlank()) {
            String[] values = value.split(",");
            ArrayUtils.trimElements(values);

            for (String element : values) {
                list.add(Integer.parseInt(element));
            }
        }

        return list;
    }

    private static String taskToString(Task task) {
        String epicId = "";

        if (task == null) {
            return null;
        } else if (task.getType() == SUBTASK) {
            Subtask subtask = (Subtask) task;
            epicId = String.valueOf(subtask.getEpicId());
        }

        return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getDuration() + "," + task.getStartTime() + "," + epicId;
    }

    private static Task taskFromString(String value) {
        Task task = null;

        if (value != null) {
            String[] values = value.split(",");
            ArrayUtils.trimElements(values);

            int id = Integer.parseInt(values[HEADER_INDEX]);
            Type type = Type.valueOf(values[1]);
            String title = values[2];
            Status status = Status.valueOf(values[3]);
            String description = values[4];
            Duration duration = Duration.parse(values[5]);
            LocalDateTime startTime = LocalDateTime.parse(values[6]);

            switch (type) {
                case SUBTASK:
                    task = new Subtask(title, description, status, Integer.parseInt(values[7]), id, duration, startTime);
                    break;
                case EPIC:
                    task = Epic.create(title, description, new HashSet<>(), id);
                    break;
                case TASK:
                    task = new Task(title, description, status, id, duration, startTime);
                    break;
                default:
                    System.out.println("Unsupported task type: " + type);
            }
        }

        return task;
    }
}
