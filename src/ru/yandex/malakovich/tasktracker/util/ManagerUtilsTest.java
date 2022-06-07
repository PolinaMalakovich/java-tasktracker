package ru.yandex.malakovich.tasktracker.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.malakovich.tasktracker.manager.InMemoryTaskManager;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ManagerUtilsTest {
    @Test
    void validateTimeWhenTasksIntersect() {
        LocalDateTime sameTime = LocalDateTime.of(2022, Month.JUNE, 6, 11, 40);
        Duration sameDuration = Duration.ofMinutes(60);
        TreeSet<Task> tasks = new TreeSet<>(TaskUtils.START_TIME_TASK_COMPARATOR);

        Task task1 = new Task("task title one",
                "task description one",
                InMemoryTaskManager.getId(),
                sameDuration,
                sameTime);
        tasks.add(task1);

        Task task2 = new Task("task title two",
                "task description two",
                InMemoryTaskManager.getId(),
                sameDuration,
                sameTime);

        assertFalse(ManagerUtils.validateTime(task2, tasks));
    }

    @ParameterizedTest
    @MethodSource("validateTimeSource")
    void validateTime(LocalDateTime left, LocalDateTime right, Task task, boolean expectedToFit) {
        assertEquals(expectedToFit, ManagerUtils.validateTime(left, right, task));
    }

    private static Stream<Arguments> validateTimeSource() {
        LocalDateTime startTime = LocalDateTime.of(2022, Month.JUNE, 7, 12, 50);
        Duration duration = Duration.of(1, ChronoUnit.DAYS);
        LocalDateTime endTime = startTime.plusDays(duration.toDays());
        Task task = new Task("title",
                "description",
                1,
                duration,
                startTime);
        return Stream.of(
                // @formatter:off
                Arguments.of(endTime                 , endTime.plusDays(1)    , task, false),
                Arguments.of(startTime.minusDays(1)  , startTime              , task, false),
                Arguments.of(null                    , endTime                , task, true ),
                Arguments.of(startTime               , null                   , task, true ),
                Arguments.of(null                    , null                   , task, true ),
                Arguments.of(startTime               , endTime                , task, true ),
                Arguments.of(startTime.minusHours(12), endTime.minusHours(12) , task, false),
                Arguments.of(startTime.plusHours(12) , endTime.plusHours(12)  , task, false),
                Arguments.of(startTime.minusHours(12), endTime.plusHours(12)  , task, true ),
                Arguments.of(startTime.plusHours(6)  , endTime.minusHours(6)  , task, false)
                // @formatter:on
        );
    }
}
