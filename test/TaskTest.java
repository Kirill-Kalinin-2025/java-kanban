import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Collections;

import static tools.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;


class TaskTest {

    TaskManager taskManager = Managers.getDefault();
    private int epicId;

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(2), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", NEW);
        final int epicId = taskManager.addEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(3, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(2), "Задачи не совпадают.");
    }

    Task t1 = new Task(0, "t1", "T", NEW);
    int t1Id = taskManager.addTask(t1);
    Task t2 = new Task(0, "t2", "T", NEW);
    int t2Id = taskManager.addTask(t2);
    Epic e1 = new Epic(0, "e1", "E", NEW);
    int e1Id = taskManager.addEpic(e1);
    Subtask s1 = new Subtask(0, "s1", "S", NEW, e1Id);
    int s1Id = taskManager.addSubtask(s1);
    Subtask s2 = new Subtask(0, "s2", "S", NEW, e1Id);
    int s2Id = taskManager.addSubtask(s2);
    Epic e2 = new Epic(0, "e2", "E", NEW);
    int e2Id = taskManager.addEpic(e2);

    @Test
    public void getNewTask() {
        Task getT1 = taskManager.getTaskById(t1Id);
        assertNotNull(getT1, "Задача не найдена.");
        assertEquals(t1, getT1, "Задачи не совпадают.");
    }

    @Test
    public void getAllNewTask() {
        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(t1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void delNewTask() {
        taskManager.delTaskById(t1Id);

        List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Удалились все задачи.");
        assertEquals(1, tasks.size(), "Неверное количество оставшихся задач.");
    }

    @Test
    public void delAllNewTask() {
        taskManager.delAllTasks();

        assertEquals(0, taskManager.getTasks().size(), "Удалены не все задачи.");
    }

    @Test
    public void getNewSubtask() {
        Subtask getS1 = taskManager.getSubtaskById(s1Id);
        assertNotNull(getS1, "Задача не найдена.");
        assertEquals(s1, getS1, "Задачи не совпадают.");
    }

    @Test
    public void getAllNewSubtask() {
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(s1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void delNewSubtask() {
        taskManager.delSubtaskById(s1Id);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Удалились все задачи.");
        assertEquals(1, subtasks.size(), "Неверное количество оставшихся задач.");
    }

    @Test
    public void delAllNewSubtask() {
        taskManager.delAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size(), "Удалены не все задачи.");
    }

    @Test
    public void getNewEpic() {
        Epic getE1 = taskManager.getEpicById(e1Id);
        assertNotNull(getE1, "Задача не найдена.");
        assertEquals(e1, getE1, "Задачи не совпадают.");
    }

    @Test
    public void getAllNewEpic() {
        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(e1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void delNewEpic() {
        taskManager.delEpicById(e1Id);

        List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Удалились все задачи.");
        assertEquals(1, epics.size(), "Неверное количество оставшихся задач.");
    }

    @Test
    public void delAllNewEpic() {
        taskManager.delAllEpics();

        assertEquals(0, taskManager.getEpics().size(), "Удалены не все задачи.");
    }

    @Test
    public void historyDuplication() {
        taskManager.getTaskById(t1Id);
        taskManager.getTaskById(t1Id);

        List<Task> history = taskManager.getHistory();
        assertNotEquals(Collections.emptyList(), history, "История пуста.");
        assertEquals(1, history.size(), "Дубль в истории.");
        assertEquals(t1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    public void historyDeleteMiddle() {
        taskManager.getTaskById(t1Id);
        taskManager.getTaskById(t2Id);
        taskManager.getEpicById(e1Id);

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Объем истории отличается от ожидаемого.");
        assertEquals(t2, history.get(1), "Отсутствует задача в середине списка.");

        taskManager.delTaskById(t2Id);
        history = taskManager.getHistory();
        assertEquals(2, history.size(), "Объем истории отличается от ожидаемого.");
        assertNotEquals(t2, history.get(0), "Задача не удалена.");
        assertNotEquals(t2, history.get(1), "Задача не удалена.");
    }

    @Test
    public void historyDeleteFirst() {
        taskManager.getTaskById(t1Id);
        taskManager.getTaskById(t2Id);
        taskManager.getEpicById(e1Id);

        List<Task> history = taskManager.getHistory();
        assertEquals(t1, history.get(0), "Отсутствует ожидаемая в начале списка задача.");

        taskManager.delTaskById(t1Id);
        history = taskManager.getHistory();
        assertNotEquals(t1, history.get(0), "Задача не удалена.");
        assertNotEquals(t1, history.get(1), "Задача не удалена.");
    }

    @Test
    public void historyDeleteEnd() {
        taskManager.getTaskById(t1Id);
        taskManager.getTaskById(t2Id);
        taskManager.getEpicById(e1Id);

        List<Task> history = taskManager.getHistory();
        assertEquals(e1, history.get(2), "Отсутствует ожидаемая в конце списка задача.");

        taskManager.delEpicById(e1Id);
        history = taskManager.getHistory();
        assertNotEquals(e1, history.get(0), "Задача не удалена.");
        assertNotEquals(e1, history.get(1), "Задача не удалена.");
    }
}