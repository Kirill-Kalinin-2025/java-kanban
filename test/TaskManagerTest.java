import exception.InputException;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static tools.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManager taskManager;
    protected Task t1, t2;
    protected Epic e1, e2;
    protected Subtask s1, s2;
    protected int t1Id, t2Id, e1Id, e2Id, s1Id, s2Id;

    abstract InMemoryTaskManager createTaskManager();

    @BeforeEach
    void setUp() throws InputException {
        taskManager = Managers.getDefault();

        t1 = new Task(0, TypeOfTask.TASK, "t1", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1));
        t1Id = taskManager.addTask(t1);

        t2 = new Task(
                0, TypeOfTask.TASK, "t1", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 17, 0), Duration.ofMinutes(1));
        t2Id = taskManager.addTask(t2);

        e1 = new Epic(0, TypeOfTask.EPIC, "e1", "d", NEW,
                null, null, new ArrayList<>());
        e1Id = taskManager.addEpic(e1);

        s1 = new Subtask(
                0, TypeOfTask.SUBTASK, "s1", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 18, 0), Duration.ofMinutes(1),
                e1Id);
        s1Id = taskManager.addSubtask(s1);

        s2 = new Subtask(
                0, TypeOfTask.SUBTASK, "s2", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 19, 0), Duration.ofMinutes(1),
                e1Id);
        s2Id = taskManager.addSubtask(s2);

        e2 = new Epic(0, TypeOfTask.EPIC, "e2", "d", NEW,
                null, null, new ArrayList<>());
        e2Id = taskManager.addEpic(e2);
    }

    @Test
    void addNewTask() throws InputException {
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
    void addNewEpic() throws InputException {
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

    @Test
    public void getNewTask() throws InputException {
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
        assertThrows(NullPointerException.class, () -> taskManager.getTaskById(t1Id), "Задача не удалилась");
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
    public void getNewSubtask() throws InputException {
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
        assertThrows(NullPointerException.class, () -> taskManager.getSubtaskById(s1Id), "Задача не удалилась");
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
        assertThrows(NullPointerException.class, () -> taskManager.getEpicById(e1Id), "Задача не удалилась");
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