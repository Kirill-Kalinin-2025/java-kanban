package tasks;

import tools.Status;
import tools.TypeOfTask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected Status status;
    protected TypeOfTask type;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = null;
        this.duration = Duration.ZERO;
    }

    public Task(String title, String description, Status status) {
        this(title, description);
        this.status = status;
    }

    public Task(int id, String title, String description, Status status) {
        this(title, description, status);
        this.id = id;
    }

    public Task(int id, TypeOfTask type, String title, String description, Status status) {
        this(id, title, description, status);
        this.type = type;
    }

    public Task(int id, TypeOfTask type, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this(id, type, title, description, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public TypeOfTask getType() {
        return type;
    }

    public void setType(TypeOfTask type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        } else {
            return startTime.plus(duration);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Tasks.Task{" +
                "id=" + id +
                ", typeOfTask=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }
}