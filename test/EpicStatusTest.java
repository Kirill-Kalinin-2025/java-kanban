import exception.InputException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tools.TypeOfTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tools.Status.DONE;
import static tools.Status.NEW;
import static tools.Status.IN_PROGRESS;

public class EpicStatusTest {
    TaskManager taskManager = Managers.getDefault();
    Epic e1 = new Epic(0, TypeOfTask.EPIC, "e1", "d",
            NEW, null, null, new ArrayList<>());
    int e1Id = taskManager.addEpic(e1);

    public EpicStatusTest() throws IOException, InterruptedException {
    }

    @Test
    public void statusEpicEmpty() {
        assertEquals(NEW, e1.getStatus(), "Статусы не совпадают.");
    }

    @Test
    public void statusEpicNewNew() throws InputException {
        Subtask s1 = new Subtask(
                0, TypeOfTask.SUBTASK, "s1", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1),
                e1Id);
        int s1Id = taskManager.addSubtask(s1);
        Subtask s2 = new Subtask(
                0, TypeOfTask.SUBTASK, "s2", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 17, 0), Duration.ofMinutes(1),
                e1Id);
        int s2Id = taskManager.addSubtask(s2);

        assertEquals(NEW, e1.getStatus(), "Статусы не совпадают.");
    }

    @Test
    public void statusEpicDoneDone() throws InputException {
        Subtask s1 = new Subtask(
                0, TypeOfTask.SUBTASK, "s1", "d", DONE,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1),
                e1Id);
        int s1Id = taskManager.addSubtask(s1);
        Subtask s2 = new Subtask(
                0, TypeOfTask.SUBTASK, "s2", "d", DONE,
                LocalDateTime.of(2025, 6, 6, 17, 0), Duration.ofMinutes(1),
                e1Id);
        int s2Id = taskManager.addSubtask(s2);

        assertEquals(DONE, s1.getStatus(), "Статусы не совпадают.");
    }

    @Test
    public void statusEpicNewDone() throws InputException {
        Subtask s1 = new Subtask(
                0, TypeOfTask.SUBTASK, "s1", "d", NEW,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1),
                e1Id);
        int s1Id = taskManager.addSubtask(s1);
        Subtask s2 = new Subtask(
                0, TypeOfTask.SUBTASK, "s2", "d", DONE,
                LocalDateTime.of(2025, 6, 6, 17, 0), Duration.ofMinutes(1),
                e1Id);
        int s2Id = taskManager.addSubtask(s2);

        assertEquals(IN_PROGRESS, taskManager.getEpicById(e1Id).getStatus(),
                "Статус эпика должен быть IN_PROGRESS, когда подзадачи имеют статусы NEW и DONE.");
    }

    @Test
    public void statusEpicProgressProgress() throws InputException {
        Subtask s1 = new Subtask(
                0, TypeOfTask.SUBTASK, "s1", "d", IN_PROGRESS,
                LocalDateTime.of(2025, 6, 6, 16, 0), Duration.ofMinutes(1),
                e1Id);
        int s1Id = taskManager.addSubtask(s1);
        Subtask s2 = new Subtask(
                0, TypeOfTask.SUBTASK, "s2", "d", IN_PROGRESS,
                LocalDateTime.of(2025, 6, 6, 17, 0), Duration.ofMinutes(1),
                e1Id);
        int s2Id = taskManager.addSubtask(s2);

        assertEquals(IN_PROGRESS, s1.getStatus(), "Статусы не совпадают.");
    }
}