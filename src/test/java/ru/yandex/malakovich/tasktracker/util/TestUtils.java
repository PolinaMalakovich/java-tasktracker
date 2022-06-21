package ru.yandex.malakovich.tasktracker.util;

import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Status;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.time.Duration;
import java.util.HashSet;

public class TestUtils {
    private static int id = 0;

    public static Epic createTestEpic(String number) {
        return Epic.create(id++, "epic description " + number, new HashSet<>(), "epic title " + number
        );
    }

    public static Task createTestTask(String number) {
        return new Task(id++, "task description " + number, "task title " + number,
                Duration.ZERO,
                null);
    }

    public static Subtask createTestSubtask(String number, int epicId) {
        return new Subtask(id++, "subtask description " + number, Status.NEW, epicId, "subtask title " + number,
                Duration.ZERO,
                null);
    }
}
