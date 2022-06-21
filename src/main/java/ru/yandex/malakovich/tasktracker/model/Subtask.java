package ru.yandex.malakovich.tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(Integer id,
                   String description,
                   Status status,
                   Integer epicId,
                   String title,
                   Duration duration,
                   LocalDateTime startTime) {
        super(id, description, status, title, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String description,
                   Status status,
                   Integer epicId,
                   String title,
                   Duration duration,
                   LocalDateTime startTime) {
        this(null, description, status, epicId, title, duration, startTime);
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public Subtask withId(Integer id) {
        return new Subtask(id, getDescription(), getStatus(), epicId, getTitle(), getDuration(), getStartTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", " + toStringHelper() +
                "}";
    }
}
