package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getId();

        if (historyMap.containsKey(taskId)) {
            removeNode(historyMap.get(taskId));
        }

        Node newNode = linkLast(task.copy());
        historyMap.put(taskId, newNode);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);

        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task);

        if (last == null) {
            first = newNode;
            last = newNode;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }

        return newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        Node current = first;

        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
        }

        node.prev = null;
        node.next = null;
    }

    private static class Node {
        private Task task;
        private Node prev;
        private Node next;

        private Node(Task task) {
            this.task = task;
        }
    }
}
