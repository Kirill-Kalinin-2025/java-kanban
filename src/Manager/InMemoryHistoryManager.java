package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MaxHistorySize = 10;
    private static final List<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
            if (history.size() > MaxHistorySize) {
                history.remove(0);
            }
        } else {
            System.out.println("Задача не добавлена в историю.");
        }
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}