package ru.yandex.malakovich.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;
import ru.yandex.malakovich.tasktracker.util.Managers;
import ru.yandex.malakovich.tasktracker.util.TestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void remove() {
        Task task = TestUtils.createTestTask("one");
        historyManager.add(task);
        historyManager.remove(task);

        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void removeNull() {
        assertDoesNotThrow(() -> historyManager.remove(null));
    }

    @Test
    void removeFromBeginning() {
        Epic epic = TestUtils.createTestEpic("one");
        Task task = TestUtils.createTestTask("one");
        Subtask subtask = TestUtils.createTestSubtask("one", epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.remove(epic);

        List<Task> taskList = List.of(task, subtask);

        assertEquals(taskList, historyManager.getHistory());
    }

    @Test
    void removeFromMiddle() {
        Epic epic = TestUtils.createTestEpic("one");
        Task task = TestUtils.createTestTask("one");
        Subtask subtask = TestUtils.createTestSubtask("one", epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.remove(task);

        List<Task> taskList = List.of(epic, subtask);

        assertEquals(taskList, historyManager.getHistory());
    }

    @Test
    void removeFromEnd() {
        Epic epic = TestUtils.createTestEpic("one");
        Task task = TestUtils.createTestTask("one");
        Subtask subtask = TestUtils.createTestSubtask("one", epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.remove(subtask);

        List<Task> taskList = List.of(epic, task);

        assertEquals(taskList, historyManager.getHistory());
    }

    @Test
    void removeFromEmptyHistory() {
        Task task = TestUtils.createTestTask("one");

        assertDoesNotThrow(() -> historyManager.remove(task));
    }

    @Test
    void addAndGetHistory() {
        Epic epic = TestUtils.createTestEpic("one");
        Task task = TestUtils.createTestTask("one");
        Subtask subtask = TestUtils.createTestSubtask("one", epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        List<Task> taskList = List.of(epic, task, subtask);

        assertEquals(taskList, historyManager.getHistory());
    }

    @Test
    void addTwice() {
        Epic epic = TestUtils.createTestEpic("one");
        Task task = TestUtils.createTestTask("one");
        Subtask subtask = TestUtils.createTestSubtask("one", epic.getId());

        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);

        List<Task> taskList = List.of(epic, task, subtask);

        assertEquals(taskList, historyManager.getHistory());
    }

    @Test
    void addNull() {
        assertDoesNotThrow(() -> historyManager.add(null));
    }

    @Test
    void getHistoryWhenEmpty() {
        assertEquals(new ArrayList<>(), historyManager.getHistory());
    }
}