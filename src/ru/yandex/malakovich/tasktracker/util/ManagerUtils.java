package ru.yandex.malakovich.tasktracker.util;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.time.LocalDateTime;
import java.util.TreeSet;

public class ManagerUtils {
    public static boolean validateTime(Task candidate, TreeSet<Task> prioritizedTasks) {
        boolean isValid = true;
        LocalDateTime lastEndTime = null;

        for (Task task : prioritizedTasks) {
            if (candidate.getId() != task.getId()) {
                if (isBeforeInclusive(candidate.getStartTime(), task.getStartTime())) {
                    isValid = validateTime(lastEndTime, task.getStartTime(), candidate);
                    break;
                }
                lastEndTime = task.getEndTime();
            }
        }

        return isValid;
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
