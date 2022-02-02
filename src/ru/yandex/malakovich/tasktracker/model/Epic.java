package ru.yandex.malakovich.tasktracker.model;

import java.util.Objects;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtasks;

    public Epic(String title, String description, Status status, Set<Integer> subtasks, int id) {
        super(title, description, status, id);
        this.subtasks = subtasks;
    }

    public Set<Integer> getSubtasks() {
        return subtasks;
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
                "} " + super.toString();
    }
}
