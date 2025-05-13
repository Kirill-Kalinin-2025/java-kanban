package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    public class Node {
        public Task task;
        public Node prev;
        public Node next;

        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    Node first;
    Node last;
    Map<Integer, Node> nodes = new HashMap<>();

    protected void linkLast(Task task) {
        Node newNode = new Node(last, task, null);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
    }

    protected List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node curNode = first;
        while (curNode != null) {
            history.add(curNode.task);
            curNode = curNode.next;
        }
        return history;
    }

    protected void removeNode(int id) {
        Node node = nodes.remove(id);
        if (node == null) {
            return;
        }
        if (node.prev == null) {
            first = first.next;
            if (first == null) {
                last = null;
            } else {
                first.prev = null;
            }
        } else if (node.next == null) {
            last = last.prev;
            if (first == null) {
                last = null;
            } else {
                last.next = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }


    @Override
    public void add(Task task) {
        if (task != null) {
            removeNode(task.getId());
            linkLast(task);
            nodes.put(task.getId(), last);
        } else {
            System.out.println("Задача не добавлена в историю.");
        }
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}