package ru.yandex.malakovich.tasktracker.model;

import ru.yandex.malakovich.tasktracker.util.DateTimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtasks;

    private Epic(String title,
                 String description,
                 Status status,
                 Set<Integer> subtasks,
                 int id,
                 Duration duration,
                 LocalDateTime startTime) {
        super(title, description, status, id, duration, startTime);
        this.subtasks = subtasks;
    }

    public Set<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubtasks(), epic.getSubtasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtasks());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", " + toStringHelper() +
                "}";
    }

    public static Epic create(String title, String description, Set<Subtask> subtasks, int id) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(description);
        Objects.requireNonNull(subtasks);

        Status status = getStatus(subtasks);
        Duration duration = getDuration(subtasks);
        LocalDateTime startTime = getStartTime(subtasks);

        Set<Integer> subtasksIds = new HashSet<>();
        for (Subtask s : subtasks) {
            subtasksIds.add(s.getId());
        }

        return new Epic(title, description, status, subtasksIds, id, duration, startTime);
    }

    private static Status getStatus(Set<Subtask> subtasks) {
        int statusNew = 0;
        int statusDone = 0;

        for (Subtask s : subtasks) {
            switch (s.getStatus()) {
                case NEW:
                    statusNew += 1;
                    break;
                case DONE:
                    statusDone += 1;
                    break;
            }
        }

        int subtasksSize = subtasks.size();
        Status status;

        if (statusNew == subtasksSize) {
            status = Status.NEW;
        } else if (statusDone == subtasksSize) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }

        return status;
    }

    private static Duration getDuration(Set<Subtask> subtasks) {
        LocalDateTime startTime = getStartTime(subtasks);
        LocalDateTime endTime = getEndTime(subtasks);
        return startTime == null || endTime == null ? null : Duration.between(startTime, endTime);
    }

    private static LocalDateTime getStartTime(Set<Subtask> subtasks) {
        LocalDateTime localDateTime = null;

        for (Subtask subtask : subtasks) {
            localDateTime = DateTimeUtils.minDate(localDateTime, subtask.getStartTime());
        }

        return localDateTime;
    }

    private static LocalDateTime getEndTime(Set<Subtask> subtasks) {
        LocalDateTime localDateTime = null;

        for (Subtask subtask : subtasks) {
            localDateTime = DateTimeUtils.maxDate(localDateTime, subtask.getEndTime());
        }

        return localDateTime;
    }
}
