package tasks;

import tools.Status;
import tools.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public class Subtask extends Task {
    private final Integer epicId;


    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;

    }

    public Subtask(int id, TypeOfTask type, String title, String description, Status status, int epicId) {
        super(id, type, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, TypeOfTask type, String title, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(id, type, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", typeOfTask=" + getType() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                ", epicId=" + epicId +
                '}';
    }
}
