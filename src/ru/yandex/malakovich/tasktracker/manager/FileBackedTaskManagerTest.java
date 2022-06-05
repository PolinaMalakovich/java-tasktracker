package ru.yandex.malakovich.tasktracker.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.malakovich.tasktracker.exception.ManagerLoadException;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static ru.yandex.malakovich.tasktracker.manager.FileBackedTaskManager.HEADER;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createManager() {
        file = createTempFile();
        return new FileBackedTaskManager(file);
    }

    @Test
    void loadFromFile() {
        Epic epic = createTestEpic("one");
        Task task = createTestTask("one");
        Subtask subtask = createTestSubtask("one", epic.getId());

        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());

        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertManagersStatesAreEqual(taskManager, managerFromFile);
    }

    @Test
    void loadFromFileWhenTaskListIsEmpty() {
        File file = createTempFile(HEADER);
        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertManagersStatesAreEqual(taskManager, managerFromFile);
    }

    @Test
    void loadFromFileWhenEpicHasNoSubtasks() {
        Epic epic = createTestEpic("one");
        Task task = createTestTask("one");

        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());

        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertManagersStatesAreEqual(taskManager, managerFromFile);
    }

    @Test
    void loadFromFileWhenHistoryIsEmpty() {
        Epic epic = createTestEpic("one");
        createTestTask("one");
        createTestSubtask("one", epic.getId());

        TaskManager managerFromFile = FileBackedTaskManager.loadFromFile(file);

        assertManagersStatesAreEqual(taskManager, managerFromFile);
    }

    @Test
    void loadFromFileThatDoesNotExist() {
        File file = createTempFile();
        boolean deleted = file.delete();
        assumeTrue(deleted, "File should have been deleted.");

        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void loadFromFileWithWrongHeader() {
        File file = createTempFile("wrong header");

        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    private static File createTempFile() {
        return createTempFile("");
    }

    private static File createTempFile(String... lines) {
        File file;
        try {
            file = File.createTempFile("taskManager", ".csv");
            Files.write(file.toPath(), List.of(lines));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        file.deleteOnExit();

        return file;
    }

    private static void assertManagersStatesAreEqual(TaskManager taskManager, TaskManager managerFromFile) {
        assertEquals(taskManager.getEpics(), managerFromFile.getEpics());
        assertEquals(taskManager.getTasks(), managerFromFile.getTasks());
        assertEquals(taskManager.getSubtasks(), managerFromFile.getSubtasks());
        assertEquals(taskManager.history(), managerFromFile.history());
    }
}
