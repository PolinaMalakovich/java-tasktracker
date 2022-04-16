package ru.yandex.malakovich.tasktracker.manager;

import ru.yandex.malakovich.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private int size = 0;
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

        size++;
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();

        for (Node node = head; node != null; node = node.next) {
            taskList.add(node.data);
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

        if (nodeToRemove.next != null) {
            nodeToRemove.next.prev = nodeToRemove.prev;
        }

        if (nodeToRemove.prev != null) {
            nodeToRemove.prev.next = nodeToRemove.next;
        }


        size--;
    }
}
