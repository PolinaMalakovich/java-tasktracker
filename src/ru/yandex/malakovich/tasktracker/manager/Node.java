package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.Objects;

public class Node {
    public Task data;
    public Node next;
    public Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    boolean hasNext() {
        return Objects.nonNull(next);
    }

    boolean hasPrev() {
        return Objects.nonNull(prev);
    }
}
