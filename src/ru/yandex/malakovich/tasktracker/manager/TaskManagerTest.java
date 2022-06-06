package ru.yandex.malakovich.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.malakovich.tasktracker.model.Epic;
import ru.yandex.malakovich.tasktracker.model.Subtask;
import ru.yandex.malakovich.tasktracker.model.Task;
import ru.yandex.malakovich.tasktracker.util.TestUtils;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createManager();

    @BeforeEach
    void beforeEach() {
        taskManager = createManager();
    }

    @Test
    void getEpicsNotEmpty() {
        Epic epic1 = createTestEpic("one");
        Epic epic2 = createTestEpic("two");
        List<Epic> epics = List.of(epic1, epic2);

        assertEquals(epics, taskManager.getEpics());
    }

    @Test
    void getEpicsEmpty() {
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void getTasksNotEmpty() {
        Task task1 = createTestTask("one");
        Task task2 = createTestTask("two");
        List<Task> tasks = List.of(task1, task2);

        assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void getTasksEmpty() {
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void getSubtasksNotEmpty() {
        Epic epic = createTestEpic("one");
        Subtask subtask1 = createTestSubtask("one", epic.getId());
        Subtask subtask2 = createTestSubtask("two", epic.getId());
        List<Subtask> subtasks = List.of(subtask1, subtask2);

        assertEquals(subtasks, taskManager.getSubtasks());
    }

    @Test
    void getSubtasksEmpty() {
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = createTestEpic("one");
        Epic epic2 = createTestEpic("two");
        createTestSubtask("one", epic1.getId());
        createTestSubtask("two", epic1.getId());
        createTestSubtask("three", epic2.getId());
        createTestSubtask("four", epic2.getId());

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteAllTasks() {
        createTestTask("one");
        createTestTask("two");

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteAllSubtasks() {
        Epic epic = createTestEpic("one");
        createTestSubtask("one", epic.getId());
        createTestSubtask("two", epic.getId());

        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void getEpicById() {
        Epic epic = createTestEpic("one");
        Epic epicFromManager = taskManager.getEpicById(epic.getId());
        assertNotNull(epicFromManager);
        assertEquals(epic, epicFromManager);
    }

    @Test
    void getEpicByWrongId() {
        assertNull(taskManager.getEpicById(-1));
    }

    @Test
    void getTaskById() {
        Task task = createTestTask("one");
        Task taskFromManager = taskManager.getTaskById(task.getId());
        assertNotNull(taskFromManager);
        assertEquals(task, taskFromManager);
    }

    @Test
    void getTaskByWrongId() {
        assertNull(taskManager.getTaskById(-1));
    }

    @Test
    void getSubtaskById() {
        Epic epic = createTestEpic("one");
        Subtask subtask = createTestSubtask("one", epic.getId());
        Subtask subtaskFromManager = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(subtaskFromManager);
        assertEquals(subtask, subtaskFromManager);
    }

    @Test
    void getSubtaskByWrongId() {
        assertNull(taskManager.getSubtaskById(-1));
    }

    @Test
    void createEpic() {
        Epic epic = createTestEpic("one");
        assertNotNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void createEpicWithNull() {
        assertDoesNotThrow(() -> taskManager.createEpic(null));
    }

    @Test
    void createTask() {
        Task task = createTestTask("one");
        assertNotNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void createTaskWithNull() {
        assertDoesNotThrow(() -> taskManager.createTask(null));
    }

    @Test
    void createSubtask() {
        Epic epic = createTestEpic("one");
        Subtask subtask = createTestSubtask("one", epic.getId());
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void createSubtaskWithNull() {
        assertDoesNotThrow(() -> taskManager.createSubtask(null));
    }

    @Test
    void updateEpic() {
        Epic oldEpic = createTestEpic("one");
        Epic epic = Epic.create(oldEpic.getTitle(),
                "new epic description",
                taskManager.getEpicSubtasks(oldEpic),
                oldEpic.getId());
        taskManager.updateEpic(epic);

        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void updateEpicWithNull() {
        Epic epic = createTestEpic("one");

        assertDoesNotThrow(() -> taskManager.updateEpic(null));
        assertNotNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void updateTask() {
        Task oldTask = createTestTask("one");
        Task task = new Task(oldTask.getTitle(),
                "new task description",
                oldTask.getStatus(),
                oldTask.getId(),
                oldTask.getDuration(),
                oldTask.getStartTime());
        taskManager.updateTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void updateTaskWithNull() {
        Task task = createTestTask("one");

        assertDoesNotThrow(() -> taskManager.updateTask(null));
        assertNotNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void updateSubtask() {
        Epic oldEpic = createTestEpic("one");
        Subtask oldSubtask = createTestSubtask("one", oldEpic.getId());
        Subtask subtask = new Subtask(oldSubtask.getTitle(),
                "new subtask description",
                oldSubtask.getStatus(),
                oldSubtask.getEpicId(),
                oldSubtask.getId(),
                oldSubtask.getDuration(),
                oldSubtask.getStartTime());
        taskManager.updateSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void updateSubtaskWithNull() {
        Epic epic = createTestEpic("one");
        Subtask subtask = createTestSubtask("one", epic.getId());

        assertDoesNotThrow(() -> taskManager.updateSubtask(null));
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void deleteEpicById() {
        Epic epic = createTestEpic("one");
        Subtask subtask = createTestSubtask("one", epic.getId());
        taskManager.deleteEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void deleteEpicByWrongId() {
        assertDoesNotThrow(() -> taskManager.deleteEpicById(-1));
    }

    @Test
    void deleteTaskById() {
        Task task = createTestTask("one");
        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void deleteTaskByWrongId() {
        assertDoesNotThrow(() -> taskManager.deleteTaskById(-1));
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = createTestEpic("one");
        Subtask subtask = createTestSubtask("one", epic.getId());
        taskManager.deleteSubtaskById(subtask.getId());

        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void deleteSubtaskByWrongId() {
        assertDoesNotThrow(() -> taskManager.deleteSubtaskById(-1));
    }

    @Test
    void getEpicSubtasksNotEmpty() {
        Epic epic = createTestEpic("one");
        Subtask subtask1 = createTestSubtask("one", epic.getId());
        Subtask subtask2 = createTestSubtask("two", epic.getId());
        Set<Subtask> subtasks = Set.of(subtask1, subtask2);

        assertEquals(subtasks, taskManager.getEpicSubtasks(taskManager.getEpicById(epic.getId())));
    }

    @Test
    void getEpicSubtasksEmpty() {
        Epic epic = createTestEpic("one");

        assertTrue(taskManager.getEpicSubtasks(epic).isEmpty());
    }

    @Test
    void getEpicSubtasksWithNull() {
        assertDoesNotThrow(() -> taskManager.getEpicSubtasks(null));
    }

    @Test
    void history() {
        Epic epic1 = createTestEpic("one");
        createTestEpic("one");

        Subtask subtask1 = createTestSubtask("one", epic1.getId());
        createTestSubtask("two", epic1.getId());

        Task task1 = createTestTask("one");
        createTestTask("two");

        Epic epic = taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        List<Task> taskList = List.of(epic, subtask1, task1);

        assertEquals(taskList, taskManager.history());
    }

    protected Epic createTestEpic(String number) {
        Epic epic = TestUtils.createTestEpic(number);
        taskManager.createEpic(epic);
        return epic;
    }

    protected Task createTestTask(String number) {
        Task task = TestUtils.createTestTask(number);
        taskManager.createTask(task);
        return task;
    }

    protected Subtask createTestSubtask(String number, int epicId) {
        Subtask subtask = TestUtils.createTestSubtask(number, epicId);
        taskManager.createSubtask(subtask);
        return subtask;
    }
}