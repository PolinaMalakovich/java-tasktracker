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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static ru.yandex.malakovich.tasktracker.model.Status.NEW;
import static ru.yandex.malakovich.tasktracker.model.Type.SUBTASK;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String HEADER = "id,type,name,status,description,epic";
    private final File file;

    private FileBackedTaskManager(File file) {
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
        super.createSubtask(subtask);
        save();
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

        int splitter = list.lastIndexOf("");

        if (splitter >= 0) {
            for (String item : list.subList(1, splitter)) {
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
                    }
                }
            }

            String history = list.get(list.size() - 1);
            List<Integer> ids = historyManagerFromString(history);

            for (Integer id : ids) {
                manager.historyManager.add(allTasks.get(id));
            }

        } else {
            throw new ManagerLoadException("Can't read form file: " + file.getName());
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

        if (value != null) {
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
                + task.getDescription() + "," + epicId;
    }

    static private Task taskFromString(String value) {
        Task task = null;

        if (value != null) {
            String[] values = value.split(",");
            ArrayUtils.trimElements(values);

            int id = Integer.parseInt(values[0]);
            Type type = Type.valueOf(values[1]);
            String title = values[2];
            Status status = Status.valueOf(values[3]);
            String description = values[4];

            switch (type) {
                case SUBTASK:
                    task = new Subtask(title, description, status, Integer.parseInt(values[5]), id);
                    break;
                case EPIC:
                    task = Epic.create(title, description, new HashSet<>(), id);
                    break;
                case TASK:
                    task = new Task(title, description, status, id);
                    break;
            }
        }

        return task;
    }

    public static void main(String[] args) throws IOException {
        Path testFile = Files.createTempFile("taskManager", ".csv");
        System.out.println("Test file: " + testFile);
        FileBackedTaskManager manager = new FileBackedTaskManager(testFile.toFile());

        Task task1 = new Task("go to the supermarket", "buy groceries for the week", getId());
        manager.createTask(task1);

        Task task2 = new Task("teach the dog a new trick", "fetch", getId());
        manager.createTask(task2);

        Epic epic1 = Epic.create("prepare for the birthday party", "see subtasks", new HashSet<>(), getId());
        manager.createEpic(epic1);

        Epic epic2 = Epic.create("complete The Last of Us II", "get all the trophies", new HashSet<>(), getId());
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("order a birthday cake", "call the bakery", NEW, epic1.getId(), getId());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("put up decorations", "blow up balloons", NEW, epic1.getId(), getId());
        manager.createSubtask(subtask2);

        manager.getTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        System.out.println("First manager");
        System.out.println();
        System.out.println("Epics: " + manager.getEpics());
        System.out.println("Tasks: " + manager.getTasks());
        System.out.println("Subtasks: " + manager.getSubtasks());
        System.out.println();
        System.out.println("History: " + manager.history());

        System.out.println("Load from file");
        TaskManager managerFromFile = loadFromFile(testFile.toFile());

        System.out.println();
        System.out.println("Manager from file");
        System.out.println();
        System.out.println("Epics: " + managerFromFile.getEpics());
        System.out.println("Tasks: " + managerFromFile.getTasks());
        System.out.println("Subtasks: " + managerFromFile.getSubtasks());
        System.out.println();
        System.out.println("History: " + managerFromFile.history());
    }
}
