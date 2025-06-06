import exception.InputException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tools.TypeOfTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tools.Status.NEW;

public class HistoryManagerTest {
    TaskManager taskManager = Managers.getDefault();
    Task t1 = new Task(
            0, TypeOfTask.TASK, "t1", "d", NEW,
            LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1));
    int t1Id = taskManager.addTask(t1);

    Task t2 = new Task(
            0, TypeOfTask.TASK, "t2", "d", NEW,
            LocalDateTime.of(2025, 6, 6, 17, 0), Duration.ofMinutes(1));
    int t2Id = taskManager.addTask(t2);

    Epic e1 = new Epic(
            0, TypeOfTask.EPIC, "e1", "d", NEW,
            null, null,
            new ArrayList<>());
    int e1Id = taskManager.addEpic(e1);

    public HistoryManagerTest() throws InputException, IOException, InterruptedException {
    }

    @Test
    public void historyEmpty() {
        assertEquals(Collections.emptyList(), taskManager.getHistory(), "История не пуста.");
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
