import exception.InputException;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tools.TypeOfTask;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tools.Status.NEW;

public class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager taskManagerFile;
    private Task t1;
    private Task t2;

    @BeforeEach
    public void setUp() throws IOException {
        file = new File("file.csv");
        taskManagerFile = new FileBackedTaskManager(file);
        t1 = new Task(1, TypeOfTask.TASK, "t1", "T", NEW,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(3));
        t2 = new Task(2, TypeOfTask.TASK, "t2", "T", NEW,
                LocalDateTime.of(2025, 6, 6, 16, 30), Duration.ofMinutes(3));
        new PrintWriter(file).close();
    }

    @Test
    void addTaskToFile() throws InputException {
        int t1Id = taskManagerFile.addTask(t1);
        String taskFromManager = taskManagerFile.getTaskById(t1Id).toString();
        String taskFromFile = FileBackedTaskManager.loadFromFile(file).getTaskById(t1Id).toString();

        assertEquals(taskFromManager, taskFromFile, "Задачи совпадают.");
    }

    @Test
    void emptyHistory() throws InputException {
        taskManagerFile.addTask(t1);
        assertTrue(taskManagerFile.getHistory().isEmpty(), "История не пуста");
    }

    @Test
    void addHistory() throws InputException {
        int t1Id = taskManagerFile.addTask(t1);
        int t2Id = taskManagerFile.addTask(t2);
        taskManagerFile.getTaskById(t1Id);
        taskManagerFile.getTaskById(t2Id);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> historyFromFile = loadedManager.getHistory();

        assertEquals(2, historyFromFile.size(), "История должна содержать 2 задачи");
        assertEquals(t1Id, historyFromFile.get(0).getId(), "Первая задача в истории должна иметь id " + t1Id);
        assertEquals(t2Id, historyFromFile.get(1).getId(), "Вторая задача в истории должна иметь id " + t2Id);
    }
}