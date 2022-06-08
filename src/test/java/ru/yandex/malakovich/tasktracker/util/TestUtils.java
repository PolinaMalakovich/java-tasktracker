package ru.yandex.malakovich.tasktracker.util;

import ru.yandex.malakovich.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Status;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.time.Duration;
import java.util.HashSet;

public class TestUtils {
    public static Epic createTestEpic(String number) {
        return Epic.create("epic title " + number,
                "epic description " + number,
                new HashSet<>(),
                InMemoryTaskManager.getId());
    }

    public static Task createTestTask(String number) {
        return new Task("task title " + number,
                "task description " + number,
                InMemoryTaskManager.getId(),
                Duration.ZERO,
                null);
    }

    public static Subtask createTestSubtask(String number, int epicId) {
        return new Subtask("subtask title " + number,
                "subtask description " + number,
                Status.NEW,
                epicId,
                InMemoryTaskManager.getId(),
                Duration.ZERO,
                null);
    }
}
