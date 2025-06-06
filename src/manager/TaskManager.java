package manager;

import exception.InputException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    Set<Task> getPrioritizedTasks();

    List<Task> getHistory();

    Integer addTask(Task task) throws InputException;

    Integer addEpic(Epic epic);

    Integer addSubtask(Subtask subtask) throws InputException;

    void updateTask(Task task) throws InputException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask) throws InputException;

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksOfEpic(Epic epic);

    Task getTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    void delAllTasks();

    void delAllEpics();

    void delAllSubtasks();

    void delTaskById(Integer id);

    void delEpicById(Integer id);

    void delSubtaskById(Integer id);
}
