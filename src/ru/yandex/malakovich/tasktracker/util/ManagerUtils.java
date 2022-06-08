package ru.yandex.malakovich.tasktracker.util;

import ru.yandex.malakovich.tasktracker.exception.InvalidStartTimeException;
import ru.yandex.malakovich.tasktracker.model.Task;

import java.time.LocalDateTime;
import java.util.Set;

public class ManagerUtils {
    public static void validateTime(Task candidate, Set<Task> prioritizedTasks) {
        boolean isValid = prioritizedTasks.isEmpty();
        Task previous = null;

        for (Task task : prioritizedTasks) {
            if (candidate.getId() != task.getId()) {
                if (isBeforeInclusive(candidate.getStartTime(), task.getStartTime())) {
                    LocalDateTime time = previous == null ? null : previous.getEndTime();
                    isValid = validateTime(time, task.getStartTime(), candidate);
                    break;
                }

                previous = task;
            } else {
                isValid = true;
            }
        }

        if (!isValid) {
            throw new InvalidStartTimeException(previous, candidate);
        }
    }

    public static boolean validateTime(LocalDateTime left, LocalDateTime right, Task task) {
        return isBeforeInclusive(left, task.getStartTime()) && isAfterInclusive(task.getEndTime(), right);
    }

    private static boolean isBeforeInclusive(LocalDateTime left, LocalDateTime right) {
        return left == null || right == null || left.equals(right) || left.isBefore(right);
    }

    private static boolean isAfterInclusive(LocalDateTime left, LocalDateTime right) {
        return left == null || right == null || right.equals(left) || right.isAfter(left);
    }
}
