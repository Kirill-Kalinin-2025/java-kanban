package Tasks;

import Tools.Status;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
        private ArrayList<Integer> subtaskId;

    public Epic(String title, String description, ArrayList<Integer> subtaskId) {
        super(title, description);
        this.subtaskId = subtaskId;
    }

    public Epic(String title, String description, Status status, ArrayList<Integer> subtaskId) {
        super(title, description, status);
        this.subtaskId = subtaskId;
    }

    public Epic(int id, String title, String description, ArrayList<Integer> subtaskId) {
        super(id, title, description);
        this.subtaskId = subtaskId;
    }

    public Epic(int id, String title, String description, Status status, ArrayList<Integer> subtaskId) {
        super(id, title, description, status);
        this.subtaskId = subtaskId;
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskId=" + subtaskId +
                '}';
    }
}
