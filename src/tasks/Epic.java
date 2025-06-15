package tasks;

import com.google.gson.annotations.SerializedName;
import tools.Status;
import tools.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected LocalDateTime endTime;
    @SerializedName("subtaskId")
    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic() {
        this.subtaskId = new ArrayList<>();
    }

    public Epic(String title, String description) {
        super(title, description);
        subtaskId = new ArrayList<>();
    }

    public Epic(String title, String description, Status status) {
        super(title, description, status);
        subtaskId = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        subtaskId = new ArrayList<>();
    }

    public Epic(int id, TypeOfTask type, String title, String description, Status status) {
        super(id, type, title, description, status);
        subtaskId = new ArrayList<>();
    }

    public Epic(int id, TypeOfTask type, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, type, title, description, status, startTime, duration);
        subtaskId = new ArrayList<>();
    }

    public Epic(int id, TypeOfTask type, String title, String description, Status status, LocalDateTime startTime, Duration duration, ArrayList<Integer> subtaskId) {
        super(id, type, title, description, status, startTime, duration);
        this.subtaskId = subtaskId;
    }

    public ArrayList<Integer> getSubtaskId() {
        return new ArrayList<>(subtaskId);
    }

    public void addSubtaskId(Integer id) {
        this.subtaskId.add(id);
    }

    public void clearSubtaskId() {
        this.subtaskId.clear();
    }

    public void removeSubtaskId(Integer id) {
        this.subtaskId.remove(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", typeOfTask=" + getType() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                ", subtaskId=" + subtaskId +
                '}';
    }
}