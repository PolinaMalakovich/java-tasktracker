package ru.yandex.malakovich.tasktracker.util;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

public class TaskUtils {
    public static final Comparator<Task> START_TIME_TASK_COMPARATOR =
            comparing(Task::getStartTime, nullsLast(naturalOrder())).thenComparing(Task::getId);
}
