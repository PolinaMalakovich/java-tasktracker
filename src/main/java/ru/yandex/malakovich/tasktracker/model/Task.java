package ru.yandex.malakovich.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String title;
    private final String description;
    private final Status status;
    private final Integer id;
    private final Duration duration;
    private final LocalDateTime startTime;

    public Task(Integer id, String description, Status status, String title, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Integer id, String description, String title, Duration duration, LocalDateTime startTime) {
        this(id, description, Status.NEW, title, duration, startTime);
    }

    public Task(String description, String title, Duration duration, LocalDateTime startTime) {
        this(null, description, Status.NEW, title, duration, startTime);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public Type getType() {
        return Type.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime == null || duration == null ? null : startTime.plusMinutes(duration.toMinutes());
    }

    public Task withId(Integer id) {
        return new Task(id, description, status, title, duration, startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId())
                && Objects.equals(getTitle(), task.getTitle())
                && Objects.equals(getDescription(), task.getDescription())
                && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getId());
    }

    @Override
    public String toString() {
        return "Task{" + toStringHelper() + '}';
    }

    String toStringHelper() {
        return "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id;
    }
}
