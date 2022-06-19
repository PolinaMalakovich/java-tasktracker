package ru.yandex.malakovich.tasktracker.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.malakovich.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.malakovich.tasktracker.manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager();
        epic = Epic.create("epic title",
                "epic description",
                new HashSet<>(),
                InMemoryTaskManager.getId());
        manager.createEpic(epic);
    }

    @Test
    void statusNewWhenNoSubtasks() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void statusNewWhenAllSubtasksAreNew() {
        createTestSubtask(Status.NEW, "one");
        createTestSubtask(Status.NEW, "two");

        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void statusDoneWhenAllSubtasksAreDone() {
        createTestSubtask(Status.DONE, "one");
        createTestSubtask(Status.DONE, "two");

        assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void statusInProgressWhenSubtasksAreNewAndDone() {
        createTestSubtask(Status.NEW, "one");
        createTestSubtask(Status.DONE, "two");

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void statusInProgressWhenSubtasksAreInProgress() {
        createTestSubtask(Status.IN_PROGRESS, "one");
        createTestSubtask(Status.IN_PROGRESS, "two");

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    private void createTestSubtask(Status status, String number, Duration duration, LocalDateTime startTime) {
        Subtask subtask = new Subtask("subtask title " + number,
                "subtask description " + number,
                status,
                epic.getId(),
                InMemoryTaskManager.getId(),
                duration,
                startTime);
        manager.createSubtask(subtask);
    }

    private void createTestSubtask(Status status, String number) {
        createTestSubtask(status, number, Duration.ZERO, null);
    }

    private void createTestSubtask(String number, Duration duration, LocalDateTime startTime) {
        createTestSubtask(Status.NEW, number, duration, startTime);
    }
}