package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final Node oldTail = tail;
        final Node newNode = new Node(tail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();

        Node node = head;
        while (node != null) {
            taskList.add(node.data);
            node = node.next;
        }

        return taskList;
    }

    private void removeNode(Node nodeToRemove) {
        if (head == null || nodeToRemove == null) {
            return;
        }

        if (head == nodeToRemove) {
            head = nodeToRemove.next;
        }

        if (tail == nodeToRemove) {
            tail = nodeToRemove.prev;
        }

        if (nodeToRemove.hasNext()) {
            nodeToRemove.next.prev = nodeToRemove.prev;
        }

        if (nodeToRemove.hasPrev()) {
            nodeToRemove.prev.next = nodeToRemove.next;
        }
    }
}
