package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private Map<Integer, Node> map = new HashMap<>();

    @Override
    public void remove(Task task) {
        if (task != null && map.containsKey(task.getId())) {
            Node node = map.remove(task.getId());
            removeNode(node);
        }
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (map.containsKey(task.getId())) {
                Node node = map.get(task.getId());
                removeNode(node);
            }

            linkLast(task);
            map.put(task.getId(), tail);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node newNode = new Node(tail, task, null);

        if (Objects.isNull(head)) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }

        tail = newNode;
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();

        Node node = head;
        while (Objects.nonNull(node)) {
            taskList.add(node.getData());
            node = node.getNext();
        }

        return taskList;
    }

    private void removeNode(Node nodeToRemove) {
        if (head == null || nodeToRemove == null) {
            return;
        }

        if (head == nodeToRemove) {
            head = nodeToRemove.getNext();
        }

        if (tail == nodeToRemove) {
            tail = nodeToRemove.getPrev();
        }

        if (nodeToRemove.hasNext()) {
            nodeToRemove.getNext().setPrev(nodeToRemove.getPrev());
        }

        if (nodeToRemove.hasPrev()) {
            nodeToRemove.getPrev().setNext(nodeToRemove.getNext());
        }
    }

    private static class Node {
        private Task data;
        private Node next;
        private Node prev;

        Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        Task getData() {
            return data;
        }

        Node getNext() {
            return next;
        }

        void setNext(Node next) {
            this.next = next;
        }

        Node getPrev() {
            return prev;
        }

        void setPrev(Node prev) {
            this.prev = prev;
        }

        boolean hasNext() {
            return Objects.nonNull(next);
        }

        boolean hasPrev() {
            return Objects.nonNull(prev);
        }
    }
}
