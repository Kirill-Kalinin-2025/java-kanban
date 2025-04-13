import Manager.HistoryManager;
import Manager.Managers;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import org.junit.jupiter.api.Test;

import java.util.List;

import static Tools.Status.NEW;
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
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
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
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", NEW, 0);
        final int subtaskId = taskManager.addSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
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
    public void updateNewTask() {
        taskManager.updateTask(t1);
        Task upT1 = taskManager.getTaskById(t1Id);

        assertEquals(t1, upT1, "Одинаковые задачи не совпадают.");

        taskManager.updateTask(new Task(0, "updateT1", "Tt", NEW));
        upT1 = taskManager.getTaskById(t1Id);

        assertNotEquals(t1, upT1, "Разные задачи совпадают.");
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
    public void updateNewSubtask() {
        taskManager.updateSubtask(s1);
        Subtask upS1 = taskManager.getSubtaskById(s1Id);

        assertEquals(s1, upS1, "Одинаковые задачи не совпадают.");

        taskManager.updateSubtask(new Subtask(0, "updateS1", "S", NEW, e1Id));

        assertNotEquals(s1, upS1, "Разные задачи совпадают.");
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
    public void updateNewEpic() {
        taskManager.updateEpic(e1);
        Epic upE1 = taskManager.getEpicById(e1Id);

        assertEquals(e1, upE1, "Одинаковые задачи не совпадают.");

        taskManager.updateEpic(new Epic(0, "updateE1", "E", NEW));
        upE1 = taskManager.getEpicById(e1Id);

        assertNotEquals(e1, upE1, "Разные задачи совпадают.");
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
}