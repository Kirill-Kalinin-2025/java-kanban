package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import Tools.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    static final private HashMap<Integer, Task> tasks = new HashMap<>();
    static final private HashMap<Integer, Epic> epics = new HashMap<>();
    static final private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;

    private int counterId() {
        id++;
        return id;
    }

    private void updateEpicInfo(Epic epic) {
        if (epic.getSubtaskId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        boolean aStatusNew = false;
        boolean aStatusInProgress = false;
        boolean aStatusDone = false;
        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;
            switch (subtask.getStatus()) {
                case NEW:
                    aStatusNew = true;
                    break;
                case IN_PROGRESS:
                    aStatusInProgress = true;
                    break;
                case DONE:
                    aStatusDone = true;
                    break;
            }
        }
        if (aStatusNew && !aStatusInProgress && !aStatusDone) {
            epic.setStatus(Status.NEW);
        } else if (aStatusDone && !aStatusNew && !aStatusInProgress) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public Integer addTask(Task task) {
        final int id = counterId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public Integer addEpic(Epic epic) {
        final int id = counterId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }


    public Integer addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            final int id = counterId();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epic.getSubtaskId().add(id);
            updateEpicInfo(epic);
            return id;
        } else {
            System.out.println("Не указан эпик");
            return -1;
        }
    }

    public boolean updateTask(Task task) {
        final int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateEpic(Epic epic) {
        final int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic savedEpic = epics.get(id);
            savedEpic.setTitle(epic.getTitle());
            savedEpic.setDescription(epic.getDescription());
            return true;
        } else {
            return false;
        }
    }

    public boolean updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final Subtask savedSubtask = subtasks.get(id);
        if (subtasks.containsKey(id)) {
            if (subtask.getEpicId() == savedSubtask.getEpicId()) {
                subtasks.put(id, subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    updateEpicInfo(epic);
                }
                return true;
            }
        }
        return false;
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Integer id) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                subtasksOfEpic.add(subtasks.get(subtaskId));
            }
        }
        return subtasksOfEpic;
    }

    public void delAllTasks() {
        tasks.clear();
    }

    public void delAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void delAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
            updateEpicInfo(epic);
        }
    }

    public void delTaskById(Integer id) {
        tasks.remove(id);
    }

    public void delEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void delSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicInfo(epic);
            subtasks.remove(id);
        }
    }
}