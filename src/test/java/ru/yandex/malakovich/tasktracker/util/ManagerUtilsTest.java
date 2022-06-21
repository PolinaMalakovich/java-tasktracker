package ru.yandex.malakovich.tasktracker.util;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.malakovich.tasktracker.exception.InvalidStartTimeException;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.malakovich.tasktracker.util.TaskUtils.START_TIME_TASK_COMPARATOR;

public class ManagerUtilsTest {
    private static int id = 0;

    @ParameterizedTest
    @MethodSource("validateSource")
    void validate(Task task, Set<Task> prioritizedTasks, boolean expectedToFit) {
        Executable validateTimeExecutable = () -> ManagerUtils.validateTime(task, prioritizedTasks);
        if (expectedToFit) {
            assertDoesNotThrow(validateTimeExecutable);
        } else {
            assertThrows(InvalidStartTimeException.class, validateTimeExecutable);
        }
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
        Task task = new Task(1, "description", "title", duration, startTime);
        return Stream.of(
                // @formatter:off
                Arguments.of(null                    , null                   , task, true ),
                Arguments.of(null                    , endTime                , task, true ),
                Arguments.of(startTime               , null                   , task, true ),
                Arguments.of(startTime               , endTime                , task, true ),
                Arguments.of(startTime.minusHours(12), endTime.plusHours(12)  , task, true ),
                Arguments.of(startTime.minusHours(12), endTime.minusHours(12) , task, false),
                Arguments.of(startTime.plusHours(12) , endTime.plusHours(12)  , task, false),
                Arguments.of(startTime.plusHours(6)  , endTime.minusHours(6)  , task, false),
                Arguments.of(endTime                 , endTime.plusDays(1)    , task, false),
                Arguments.of(startTime.minusDays(1)  , startTime              , task, false)
                // @formatter:on
        );
    }

    private static Stream<Arguments> validateSource() {
        Duration sixtyMin = Duration.ofMinutes(60);
        Duration twentyMin = Duration.ofMinutes(20);

        return Stream.of(
                // @formatter:off
                Arguments.of(task(null, null)           , taskSet(task(null, null))          , true ),
                Arguments.of(task(null, null)           , taskSet(task(at(16, 0), sixtyMin)) , true ),
                Arguments.of(task(at(15, 30), sixtyMin) , taskSet(task(null, null))          , true ),
                Arguments.of(task(at(16, 0), sixtyMin)  , taskSet()                          , true ),
                Arguments.of(task(at(16, 0), sixtyMin)  , taskSet(task(at(16, 0), sixtyMin)) , false),
                Arguments.of(task(at(16, 20), twentyMin), taskSet(task(at(16, 0), sixtyMin)) , false),
                Arguments.of(task(at(15, 40), sixtyMin) , taskSet(task(at(16, 0), twentyMin)), false),
                Arguments.of(task(at(16, 30), sixtyMin) , taskSet(task(at(16, 0), sixtyMin)) , false),
                Arguments.of(task(at(15, 30), sixtyMin) , taskSet(task(at(16, 0), sixtyMin)) , false)
                // @formatter:on
        );
    }

    private static Task task(LocalDateTime startTime, Duration duration) {
        return new Task(id++, "description", "title", duration, startTime);
    }

    private static LocalDateTime at(int hours, int minutes) {
        return LocalDateTime.of(2022, Month.JUNE, 8, hours, minutes);
    }

    private static Set<Task> taskSet(Task... tasks) {
        Set<Task> taskSet = new TreeSet<>(START_TIME_TASK_COMPARATOR);
        taskSet.addAll(Arrays.asList(tasks));
        return taskSet;
    }
}
