package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    Integer addTask(Task task);

    Integer addEpic(Epic epic);

    Integer addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

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
